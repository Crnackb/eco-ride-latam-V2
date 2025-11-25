package com.ecoride.notification.web;

import com.ecoride.notification.domain.Outbox;
import com.ecoride.notification.domain.Template;
import com.ecoride.notification.repository.OutboxRepository;
import com.ecoride.notification.repository.TemplateRepository;
import com.ecoride.notification.web.dto.NotifyRequest;
import com.ecoride.notification.web.dto.NotifyResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final TemplateRepository templateRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotifyResponse notify(@RequestBody NotifyRequest request) {
        Template template = templateRepository.findByCode(request.getTemplateCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found"));

        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("to", request.getTo());
        payloadMap.put("templateCode", template.getCode());
        payloadMap.put("channel", template.getChannel());
        payloadMap.put("subject", template.getSubject());
        payloadMap.put("body", template.getBody());
        payloadMap.put("params", request.getParams());

        String payload;
        try {
            payload = objectMapper.writeValueAsString(payloadMap);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error serializing payload", e);
        }

        Outbox outbox = new Outbox();
        outbox.setEventType("NOTIFICATION");
        outbox.setPayload(payload);
        outbox.setStatus("pending");
        outbox.setRetries(0);
        outbox.setCreatedAt(LocalDateTime.now());

        outbox = outboxRepository.save(outbox);

        return new NotifyResponse(outbox.getId(), outbox.getStatus());
    }
}
