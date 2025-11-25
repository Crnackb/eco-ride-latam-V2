package com.ecoride.payment.messaging;

public final class KafkaTopics {

    public static final String RESERVATION_REQUESTS = "reservation-requests";
    public static final String PAYMENT_EVENTS = "payment-events";
    public static final String RESERVATION_EVENTS = "reservation-events";

    private KafkaTopics() {
    }
}
