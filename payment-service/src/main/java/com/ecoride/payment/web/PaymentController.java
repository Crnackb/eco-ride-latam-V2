package com.ecoride.payment.web;

import com.ecoride.payment.domain.Charge;
import com.ecoride.payment.domain.PaymentIntent;
import com.ecoride.payment.domain.Refund;
import com.ecoride.payment.repository.ChargeRepository;
import com.ecoride.payment.repository.PaymentIntentRepository;
import com.ecoride.payment.repository.RefundRepository;
import com.ecoride.payment.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentIntentRepository paymentIntentRepository;
    private final ChargeRepository chargeRepository;
    private final RefundRepository refundRepository;

    @PostMapping("/intent")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentIntentResponse createIntent(@RequestBody CreatePaymentIntentRequest request) {
        PaymentIntent intent = new PaymentIntent();
        intent.setReservationId(request.getReservationId());
        intent.setAmount(request.getAmount());
        intent.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        // Simplificación: lo marcamos como authorized directamente
        intent.setStatus("authorized");
        intent.setCreatedAt(LocalDateTime.now());

        intent = paymentIntentRepository.save(intent);

        return new PaymentIntentResponse(
                intent.getId(),
                intent.getReservationId(),
                intent.getAmount(),
                intent.getCurrency(),
                intent.getStatus()
        );
    }

    @PostMapping("/capture/{intentId}")
    public PaymentCaptureResponse capture(@PathVariable Long intentId) {
        PaymentIntent intent = paymentIntentRepository.findById(intentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PaymentIntent not found"));

        if (!"authorized".equalsIgnoreCase(intent.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PaymentIntent not in authorized status");
        }

        intent.setStatus("captured");
        paymentIntentRepository.save(intent);

        Charge charge = new Charge();
        charge.setPaymentIntentId(intent.getId());
        charge.setProvider("DUMMY");
        charge.setProviderRef(UUID.randomUUID().toString());
        charge.setCapturedAt(LocalDateTime.now());
        charge = chargeRepository.save(charge);

        return new PaymentCaptureResponse(intent.getId(), charge.getId(), intent.getStatus());
    }

    @PostMapping("/refund/{chargeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public RefundResponse refund(@PathVariable Long chargeId, @RequestBody RefundRequest request) {
        Charge charge = chargeRepository.findById(chargeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Charge not found"));

        Refund refund = new Refund();
        refund.setChargeId(charge.getId());
        refund.setAmount(request.getAmount());
        refund.setReason(request.getReason());
        refund.setCreatedAt(LocalDateTime.now());
        refund = refundRepository.save(refund);

        return new RefundResponse(
                refund.getId(),
                refund.getChargeId(),
                refund.getAmount(),
                refund.getReason()
        );
    }

    @GetMapping
    public List<PaymentIntent> getPaymentIntents() {
        return paymentIntentRepository.findAll();
    }
}