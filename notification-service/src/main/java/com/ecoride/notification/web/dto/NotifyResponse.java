package com.ecoride.notification.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotifyResponse {
    private Long outboxId;
    private String status;
}