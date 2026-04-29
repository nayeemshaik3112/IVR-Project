package com.example.ivr.claims.dto;

import java.time.Instant;

public record ClaimEventMessage(
        String eventId,
        String claimId,
        String customerId,   // ✅ FIX: Long → String (matches ClaimCreatedEvent)
        String policyNumber,
        String status,
        String description,
        Instant occurredAt,
        String eventType
) {
}