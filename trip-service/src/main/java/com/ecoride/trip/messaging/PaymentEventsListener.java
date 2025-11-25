package com.ecoride.trip.messaging;

import com.ecoride.trip.domain.Reservation;
import com.ecoride.trip.domain.ReservationStatus;
import com.ecoride.trip.domain.Trip;
import com.ecoride.trip.messaging.events.PaymentAuthorizedEvent;
import com.ecoride.trip.messaging.events.PaymentFailedEvent;
import com.ecoride.trip.repository.ReservationRepository;
import com.ecoride.trip.repository.TripRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventsListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventsListener.class);

    private final ObjectMapper objectMapper;
    private final ReservationRepository reservationRepository;
    private final TripRepository tripRepository;
    private final ReservationEventProducer reservationEventProducer;

    @KafkaListener(topics = KafkaTopics.PAYMENT_EVENTS)
    public void onPaymentEvent(String message) {
        try {
            // 1) Leemos como JsonNode para inspeccionar el contenido
            JsonNode root = objectMapper.readTree(message);
            log.info("Received payment event: {}", message);

            // 2) Si tiene "reason" => es un PaymentFailedEvent
            if (root.has("reason")) {
                PaymentFailedEvent failed =
                        objectMapper.treeToValue(root, PaymentFailedEvent.class);
                handlePaymentFailed(failed);
            } else {
                // 3) Si no, lo tratamos como PaymentAuthorizedEvent
                PaymentAuthorizedEvent authorized =
                        objectMapper.treeToValue(root, PaymentAuthorizedEvent.class);
                handlePaymentAuthorized(authorized);
            }

        } catch (Exception e) {
            log.error("Error processing payment event: {}", message, e);
        }
    }

    private void handlePaymentAuthorized(PaymentAuthorizedEvent event) {
        log.info("Handling PaymentAuthorized for reservation {}", event.getReservationId());

        Reservation reservation = reservationRepository.findById(event.getReservationId())
                .orElse(null);
        if (reservation == null) {
            log.warn("Reservation {} not found", event.getReservationId());
            return;
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        reservationEventProducer.publishReservationConfirmed(event.getReservationId());
    }

    private void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("Handling PaymentFailed for reservation {}", event.getReservationId());

        Reservation reservation = reservationRepository.findById(event.getReservationId())
                .orElse(null);
        if (reservation == null) {
            log.warn("Reservation {} not found", event.getReservationId());
            return;
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        Trip trip = tripRepository.findById(reservation.getTripId()).orElse(null);
        if (trip != null && trip.getSeatsAvailable() != null) {
            trip.setSeatsAvailable(trip.getSeatsAvailable() + 1);
            tripRepository.save(trip);
        }

        reservationEventProducer.publishReservationCancelled(
                event.getReservationId(),
                event.getReason()
        );
    }
}