package com.example.ivr.notification.dto;

public record CommunicationResolutionResponse(
        Long customerId,
        String channel,
        String reason,
        boolean quietHoursApplied
) {
}