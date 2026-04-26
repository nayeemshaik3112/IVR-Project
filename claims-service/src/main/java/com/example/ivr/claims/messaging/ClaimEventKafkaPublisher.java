package com.example.ivr.claims.messaging;

import com.example.ivr.claims.config.KafkaTopicConfig;
import com.example.ivr.claims.dto.ClaimEventMessage;
import com.example.ivr.claims.outbox.ClaimEventOutbox;
import com.example.ivr.claims.outbox.ClaimEventOutboxService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ClaimEventKafkaPublisher {

    private static final Logger log = LoggerFactory.getLogger(ClaimEventKafkaPublisher.class);

    private final KafkaTemplate<String, ClaimEventMessage> kafkaTemplate;
    private final ClaimEventOutboxService outboxService;
    private final Counter publishSuccessCounter;
    private final Counter publishFailureCounter;

    public ClaimEventKafkaPublisher(KafkaTemplate<String, ClaimEventMessage> kafkaTemplate,
                                    ClaimEventOutboxService outboxService,
                                    MeterRegistry meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.outboxService = outboxService;
        this.publishSuccessCounter = meterRegistry.counter("claims.kafka.publish.success");
        this.publishFailureCounter = meterRegistry.counter("claims.kafka.publish.failure");
    }

    @Scheduled(fixedDelayString = "${claims.outbox.publish-interval:5000}")
    public void publishPendingEvents() {
        List<ClaimEventOutbox> batch = outboxService.claimBatch(25);
        if (batch.isEmpty()) {
            return;
        }
        log.info("claim_outbox_dispatch_started batchSize={}", batch.size());
        batch.forEach(this::publish);
    }

    private void publish(ClaimEventOutbox outbox) {
        ClaimEventMessage message = outboxService.deserialize(outbox);
        kafkaTemplate.send(KafkaTopicConfig.CLAIM_EVENTS_TOPIC, message.claimId(), message)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        publishFailureCounter.increment();
                        outboxService.markFailed(outbox.getId(), throwable.toString());
                        log.error("claim_event_publish_failed eventId={} claimId={} reason={}",
                                message.eventId(), message.claimId(), throwable.toString());
                        return;
                    }
                    publishSuccessCounter.increment();
                    outboxService.markPublished(outbox.getId());
                    log.info("claim_event_published eventId={} claimId={} topic={} partition={} offset={}",
                            message.eventId(),
                            message.claimId(),
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                });
    }
}
