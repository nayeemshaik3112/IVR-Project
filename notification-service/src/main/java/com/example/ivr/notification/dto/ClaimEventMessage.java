package com.example.ivr.notification.dto;

import java.time.Instant;

public record ClaimEventMessage(
        String eventId,
        String claimId,
        Long customerId,
        String policyNumber,
        String status,
        String description,
        Instant occurredAt,
        String eventType
) {
}
