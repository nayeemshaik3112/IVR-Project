package com.example.ivr.communication.dto;

public record CommunicationPreferenceResponse(
        Long customerId,
        String preferredChannel,
        boolean smsOptIn,
        boolean emailOptIn,
        boolean voiceOptIn,
        String quietHoursStart,
        String quietHoursEnd,
        String escalationChannel,
        String languageCode
) {
}