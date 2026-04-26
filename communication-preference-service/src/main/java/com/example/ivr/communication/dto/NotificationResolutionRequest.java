package com.example.ivr.communication.dto;

public record NotificationResolutionRequest(
        Long customerId,
        String requestedChannel,
        String eventType
) {
}