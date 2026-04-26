package com.example.ivr.communication.dto;

public record CommunicationResolutionResponse(
        Long customerId,
        String channel,
        String reason,
        boolean quietHoursApplied
) {
}