package com.ecoride.payment.repository;

import com.ecoride.payment.domain.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, Long> {
}
