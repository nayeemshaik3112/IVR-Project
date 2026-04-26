package com.example.ivr.communication.dto;

import com.example.ivr.communication.entity.NotificationChannel;
import java.time.LocalTime;

public record CommunicationPreferenceRequest(
        Long customerId,
        NotificationChannel preferredChannel,
        boolean smsOptIn,
        boolean emailOptIn,
        boolean voiceOptIn,
        LocalTime quietHoursStart,
        LocalTime quietHoursEnd,
        NotificationChannel escalationChannel,
        String languageCode
) {
}