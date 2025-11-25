package com.ecoride.payment.web.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundRequest {
    private BigDecimal amount;
    private String reason;
}
