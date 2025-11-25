package com.ecoride.notification.web.dto;

import lombok.Data;

import java.util.Map;

@Data
public class NotifyRequest {
    private String templateCode;
    private String to;
    private Map<String, String> params;
}