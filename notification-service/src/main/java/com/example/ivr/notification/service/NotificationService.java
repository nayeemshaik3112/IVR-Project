package com.example.ivr.notification.service;

import com.example.ivr.notification.client.CommunicationPreferenceClient;
import com.example.ivr.notification.dto.ClaimEventMessage;
import com.example.ivr.notification.dto.CommunicationResolutionResponse;
import com.example.ivr.notification.dto.NotificationResolutionRequest;
import com.example.ivr.notification.dto.NotificationResponse;
import com.example.ivr.notification.entity.NotificationLog;
import com.example.ivr.notification.repository.NotificationLogRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationLogRepository notificationLogRepository;
    private final CommunicationPreferenceClient communicationPreferenceClient;
    private final Counter processedCounter;
    private final Counter duplicateCounter;
    private final Counter dlqCounter;

    public NotificationService(NotificationLogRepository notificationLogRepository,
                               CommunicationPreferenceClient communicationPreferenceClient,
                               MeterRegistry meterRegistry) {
        this.notificationLogRepository = notificationLogRepository;
        this.communicationPreferenceClient = communicationPreferenceClient;
        this.processedCounter = meterRegistry.counter("notification.events.processed");
        this.duplicateCounter = meterRegistry.counter("notification.events.duplicate");
        this.dlqCounter = meterRegistry.counter("notification.events.dlt");
    }

    public void processClaimEvent(ClaimEventMessage event) {
        if (notificationLogRepository.findByEventId(event.eventId()).isPresent()) {
            duplicateCounter.increment();
            log.info("notification_duplicate_event_skipped eventId={} claimId={}", event.eventId(), event.claimId());
            return;
        }

        CommunicationResolutionResponse resolution = resolveChannel(event);

        NotificationLog notificationLog = new NotificationLog();
        notificationLog.setEventId(event.eventId());
        notificationLog.setClaimId(event.claimId());
        notificationLog.setEventType(event.eventType());
        notificationLog.setStatus(event.status());
        notificationLog.setProcessingState("PROCESSED");
        notificationLog.setProcessedAt(Instant.now());
        notificationLog.setMessage("Notification prepared for claim %s with status %s via %s. %s".formatted(
                event.claimId(), event.status(), resolution.channel(), resolution.reason()));
        notificationLogRepository.save(notificationLog);
        processedCounter.increment();
        log.info("notification_event_processed eventId={} claimId={} status={} channel={}",
                event.eventId(), event.claimId(), event.status(), resolution.channel());
    }

    public void markAsDeadLetter(ClaimEventMessage event, String reason) {
        if (notificationLogRepository.findByEventId(event.eventId()).isPresent()) {
            return;
        }
        NotificationLog notificationLog = new NotificationLog();
        notificationLog.setEventId(event.eventId());
        notificationLog.setClaimId(event.claimId());
        notificationLog.setEventType(event.eventType());
        notificationLog.setStatus(event.status());
        notificationLog.setProcessingState("DLQ");
        notificationLog.setProcessedAt(Instant.now());
        notificationLog.setMessage("Notification routed to DLQ due to: " + reason);
        notificationLogRepository.save(notificationLog);
        dlqCounter.increment();
        log.error("notification_event_dead_lettered eventId={} claimId={} reason={}",
                event.eventId(), event.claimId(), reason);
    }

    public List<NotificationResponse> getAll() {
        return notificationLogRepository.findAll().stream()
                .map(log -> new NotificationResponse(
                        log.getId(),
                        log.getEventId(),
                        log.getClaimId(),
                        log.getEventType(),
                        log.getStatus(),
                        log.getProcessingState(),
                        log.getProcessedAt(),
                        log.getMessage()
                ))
                .toList();
    }

    private CommunicationResolutionResponse resolveChannel(ClaimEventMessage event) {
        try {
            return communicationPreferenceClient.resolve(new NotificationResolutionRequest(
                    event.customerId(), "SMS", event.eventType()));
        } catch (RuntimeException exception) {
            log.warn("notification_preference_lookup_failed customerId={} reason={}",
                    event.customerId(), exception.toString());
            return new CommunicationResolutionResponse(event.customerId(), "SMS",
                    "Fallback to SMS because preference lookup failed", false);
        }
    }
}