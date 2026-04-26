package com.example.ivr.notification.dto;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        String eventId,
        String claimId,
        String eventType,
        String status,
        String processingState,
        Instant processedAt,
        String message
) {
}
