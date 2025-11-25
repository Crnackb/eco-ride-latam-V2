package com.ecoride.payment.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "charge")
@Data
@NoArgsConstructor
@Getter
@Setter
public class Charge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_intent_id", nullable = false)
    private Long paymentIntentId;

    @Column(nullable = false)
    private String provider;

    @Column(name = "provider_ref", nullable = false)
    private String providerRef;

    @Column(name = "captured_at")
    private LocalDateTime capturedAt;
}
