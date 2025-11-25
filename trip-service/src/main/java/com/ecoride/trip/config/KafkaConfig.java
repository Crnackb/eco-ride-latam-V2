package com.ecoride.trip.config;

import com.ecoride.trip.messaging.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public NewTopic reservationRequestsTopic() {
        return TopicBuilder.name(KafkaTopics.RESERVATION_REQUESTS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentEventsTopic() {
        return TopicBuilder.name(KafkaTopics.PAYMENT_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic reservationEventsTopic() {
        return TopicBuilder.name(KafkaTopics.RESERVATION_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}