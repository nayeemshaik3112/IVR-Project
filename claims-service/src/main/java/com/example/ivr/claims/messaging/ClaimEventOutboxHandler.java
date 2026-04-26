package com.example.ivr.claims.messaging;

import com.example.ivr.claims.command.event.ClaimCreatedEvent;
import com.example.ivr.claims.command.event.ClaimStatusUpdatedEvent;
import com.example.ivr.claims.dto.ClaimEventMessage;
import com.example.ivr.claims.outbox.ClaimEventOutboxService;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
public class ClaimEventOutboxHandler {

    private final ClaimEventOutboxService outboxService;

    public ClaimEventOutboxHandler(ClaimEventOutboxService outboxService) {
        this.outboxService = outboxService;
    }

    @EventHandler
    public void on(ClaimCreatedEvent event) {
        outboxService.store(new ClaimEventMessage(
                buildEventId("ClaimCreatedEvent", event.getClaimId(), event.getOccurredAt().toString()),
                event.getClaimId(),
                event.getCustomerId(),
                event.getPolicyNumber(),
                event.getStatus(),
                event.getDescription(),
                event.getOccurredAt(),
                "ClaimCreatedEvent"
        ));
    }

    @EventHandler
    public void on(ClaimStatusUpdatedEvent event) {
        outboxService.store(new ClaimEventMessage(
                buildEventId("ClaimStatusUpdatedEvent", event.getClaimId(), event.getOccurredAt().toString()),
                event.getClaimId(),
                null,
                null,
                event.getStatus(),
                null,
                event.getOccurredAt(),
                "ClaimStatusUpdatedEvent"
        ));
    }

    private String buildEventId(String eventType, String claimId, String occurredAt) {
        return eventType + ":" + claimId + ":" + occurredAt;
    }
}
