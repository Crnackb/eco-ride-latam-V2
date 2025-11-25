package com.ecoride.payment.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentCaptureResponse {
    private Long paymentIntentId;
    private Long chargeId;
    private String status;
}
