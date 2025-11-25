package com.ecoride.payment.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestedEvent {

    private Long reservationId;
    private Long tripId;
    private Long passengerId;
    private BigDecimal amount;
}
