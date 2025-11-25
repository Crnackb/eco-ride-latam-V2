package com.ecoride.payment.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAuthorizedEvent {

    private Long reservationId;
    private Long paymentIntentId;
    private Long chargeId;
}
