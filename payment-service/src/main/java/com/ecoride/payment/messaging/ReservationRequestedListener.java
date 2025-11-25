package com.ecoride.payment.messaging;

import com.ecoride.payment.domain.Charge;
import com.ecoride.payment.domain.PaymentIntent;
import com.ecoride.payment.messaging.events.PaymentAuthorizedEvent;
import com.ecoride.payment.messaging.events.PaymentFailedEvent;
import com.ecoride.payment.messaging.events.ReservationRequestedEvent;
import com.ecoride.payment.repository.ChargeRepository;
import com.ecoride.payment.repository.PaymentIntentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationRequestedListener {

    private static final Logger log = LoggerFactory.getLogger(ReservationRequestedListener.class);

    private final ObjectMapper objectMapper;
    private final PaymentIntentRepository paymentIntentRepository;
    private final ChargeRepository chargeRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = KafkaTopics.RESERVATION_REQUESTS)
    public void onReservationRequested(String message) {
        ReservationRequestedEvent event;
        try {
            event = objectMapper.readValue(message, ReservationRequestedEvent.class);
        } catch (Exception e) {
            log.error("Error deserializing ReservationRequestedEvent: {}", message, e);
            return;
        }

        try {
            log.info("Processing ReservationRequested {}", event.getReservationId());

            PaymentIntent intent = new PaymentIntent();
            intent.setReservationId(event.getReservationId());
            intent.setAmount(event.getAmount());
            intent.setCurrency("USD");
            intent.setCreatedAt(LocalDateTime.now());

            boolean shouldFail = event.getAmount().compareTo(new BigDecimal("10")) < 0;

            if (shouldFail) {
                intent.setStatus("FAILED");
                paymentIntentRepository.save(intent);

                PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                        event.getReservationId(),
                        "AMOUNT_TOO_LOW"
                );
                sendEvent(KafkaTopics.PAYMENT_EVENTS, failedEvent);
                log.info("Payment failed for reservation {} due to business rule (amount < 10)",
                        event.getReservationId());
                return;
            }

            intent.setStatus("AUTHORIZED");
            PaymentIntent savedIntent = paymentIntentRepository.save(intent);

            Charge charge = new Charge();
            charge.setPaymentIntentId(savedIntent.getId());
            charge.setProvider("FAKE_PROVIDER");
            charge.setProviderRef("CHARGE-" + savedIntent.getId() + "-" + System.currentTimeMillis());
            charge.setCapturedAt(LocalDateTime.now());
            Charge savedCharge = chargeRepository.save(charge);

            PaymentAuthorizedEvent authorizedEvent = new PaymentAuthorizedEvent(
                    event.getReservationId(),
                    savedIntent.getId(),
                    savedCharge.getId()
            );

            sendEvent(KafkaTopics.PAYMENT_EVENTS, authorizedEvent);

        } catch (Exception e) {
            // FALLO TÉCNICO
            log.error("Error processing reservation {}, publishing PaymentFailed",
                    event.getReservationId(), e);

            PaymentFailedEvent failedEvent = new PaymentFailedEvent(
                    event.getReservationId(),
                    "PAYMENT_ERROR"
            );
            sendEvent(KafkaTopics.PAYMENT_EVENTS, failedEvent);
        }
    }

    private void sendEvent(String topic, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, payload);
            log.info("Published event to {}: {}", topic, payload);
        } catch (JsonProcessingException e) {
            log.error("Error serializing payment event", e);
        }
    }
}