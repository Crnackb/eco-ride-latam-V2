package com.ecoride.trip.messaging;

import com.ecoride.trip.domain.Reservation;
import com.ecoride.trip.domain.Trip;
import com.ecoride.trip.messaging.events.ReservationCancelledEvent;
import com.ecoride.trip.messaging.events.ReservationConfirmedEvent;
import com.ecoride.trip.messaging.events.ReservationRequestedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationEventProducer {

    private static final Logger log = LoggerFactory.getLogger(ReservationEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishReservationRequested(Reservation reservation, Trip trip) {
        ReservationRequestedEvent event = new ReservationRequestedEvent(
                reservation.getId(),
                trip.getId(),
                reservation.getPassengerId(),
                trip.getPrice()
        );
        send(KafkaTopics.RESERVATION_REQUESTS, event);
    }

    public void publishReservationConfirmed(Long reservationId) {
        ReservationConfirmedEvent event = new ReservationConfirmedEvent(reservationId);
        send(KafkaTopics.RESERVATION_EVENTS, event);
    }

    public void publishReservationCancelled(Long reservationId, String reason) {
        ReservationCancelledEvent event = new ReservationCancelledEvent(reservationId, reason);
        send(KafkaTopics.RESERVATION_EVENTS, event);
    }

    private void send(String topic, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, payload);
            log.info("Published event to {}: {}", topic, payload);
        } catch (JsonProcessingException e) {
            log.error("Error serializing Kafka event", e);
            throw new IllegalStateException("Error serializing Kafka event", e);
        }
    }
}
