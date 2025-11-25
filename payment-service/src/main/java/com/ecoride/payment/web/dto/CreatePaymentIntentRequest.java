package com.ecoride.payment.web.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePaymentIntentRequest {
    private Long reservationId;
    private BigDecimal amount;
    private String currency;
}
