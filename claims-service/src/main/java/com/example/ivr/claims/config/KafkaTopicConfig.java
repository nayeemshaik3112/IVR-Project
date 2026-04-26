package com.example.ivr.claims.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String CLAIM_EVENTS_TOPIC = "claim-events";

    @Bean
    public NewTopic claimEventsTopic() {
        return TopicBuilder.name(CLAIM_EVENTS_TOPIC).partitions(3).replicas(1).build();
    }
}
