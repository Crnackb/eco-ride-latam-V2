package com.ecoride.payment.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentIntentResponse {
    private Long id;
    private Long reservationId;
    private BigDecimal amount;
    private String currency;
    private String status;
}
