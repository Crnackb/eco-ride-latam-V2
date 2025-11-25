package com.ecoride.gateway.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    private Mono<ResponseEntity<Map<String, Object>>> fallback(String service) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", service + " no está disponible. Inténtalo más tarde.");
        body.put("service", service);
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
    }

    @GetMapping("/fallback/trip-service")
    public Mono<ResponseEntity<Map<String, Object>>> tripFallback() {
        return fallback("trip-service");
    }

    @GetMapping("/fallback/passenger-service")
    public Mono<ResponseEntity<Map<String, Object>>> passengerFallback() {
        return fallback("passenger-service");
    }

    @GetMapping("/fallback/payment-service")
    public Mono<ResponseEntity<Map<String, Object>>> paymentFallback() {
        return fallback("payment-service");
    }

    @GetMapping("/fallback/notification-service")
    public Mono<ResponseEntity<Map<String, Object>>> notificationFallback() {
        return fallback("notification-service");
    }
}