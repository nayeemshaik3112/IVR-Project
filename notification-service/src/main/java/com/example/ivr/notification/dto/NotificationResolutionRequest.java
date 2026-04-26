package com.example.ivr.notification.dto;

public record NotificationResolutionRequest(
        Long customerId,
        String requestedChannel,
        String eventType
) {
}