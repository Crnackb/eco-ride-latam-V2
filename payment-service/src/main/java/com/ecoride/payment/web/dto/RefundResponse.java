package com.ecoride.payment.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RefundResponse {
    private Long refundId;
    private Long chargeId;
    private BigDecimal amount;
    private String reason;
}
