package com.example.ivr.notification.messaging;

import com.example.ivr.notification.dto.ClaimEventMessage;
import com.example.ivr.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class ClaimEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ClaimEventConsumer.class);

    private final NotificationService notificationService;

    public ClaimEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @org.springframework.retry.annotation.Backoff(delay = 1000, multiplier = 2.0),
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = "claim-events", groupId = "notification-service")
    public void consume(ClaimEventMessage message) {
        notificationService.processClaimEvent(message);
    }

    //final handler for messages that failed all retries
    @DltHandler
    public void deadLetter(ClaimEventMessage message,
                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("notification_event_received_on_dlt eventId={} topic={}", message.eventId(), topic);
        notificationService.markAsDeadLetter(message, "Retries exhausted on topic " + topic);
    }
}
