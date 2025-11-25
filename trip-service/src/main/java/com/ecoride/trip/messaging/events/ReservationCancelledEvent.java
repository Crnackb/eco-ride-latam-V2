package com.ecoride.trip.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCancelledEvent {

    private Long reservationId;
    private String reason;
}