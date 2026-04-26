package com.example.ivr.communication.service;

import com.example.ivr.communication.dto.CommunicationPreferenceRequest;
import com.example.ivr.communication.dto.CommunicationPreferenceResponse;
import com.example.ivr.communication.dto.CommunicationResolutionResponse;
import com.example.ivr.communication.dto.NotificationResolutionRequest;
import com.example.ivr.communication.entity.CommunicationPreference;
import com.example.ivr.communication.entity.NotificationChannel;
import com.example.ivr.communication.exception.CommunicationPreferenceNotFoundException;
import com.example.ivr.communication.exception.CommunicationPreferenceValidationException;
import com.example.ivr.communication.repository.CommunicationPreferenceRepository;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommunicationPreferenceService {

    private final CommunicationPreferenceRepository repository;

    public CommunicationPreferenceService(CommunicationPreferenceRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CommunicationPreferenceResponse upsert(CommunicationPreferenceRequest request) {
        validate(request);
        CommunicationPreference preference = repository.findByCustomerId(request.customerId())
                .orElseGet(CommunicationPreference::new);
        preference.setCustomerId(request.customerId());
        preference.setPreferredChannel(request.preferredChannel());
        preference.setSmsOptIn(request.smsOptIn());
        preference.setEmailOptIn(request.emailOptIn());
        preference.setVoiceOptIn(request.voiceOptIn());
        preference.setQuietHoursStart(request.quietHoursStart());
        preference.setQuietHoursEnd(request.quietHoursEnd());
        preference.setEscalationChannel(request.escalationChannel());
        preference.setLanguageCode(request.languageCode());
        repository.save(preference);
        return map(preference);
    }

    @Transactional(readOnly = true)
    public CommunicationPreferenceResponse get(Long customerId) {
        return repository.findByCustomerId(customerId)
                .map(this::map)
                .orElseThrow(() -> new CommunicationPreferenceNotFoundException(customerId));
    }

    @Transactional(readOnly = true)
    public CommunicationResolutionResponse resolve(NotificationResolutionRequest request) {
        CommunicationPreference preference = repository.findByCustomerId(request.customerId())
                .orElseThrow(() -> new CommunicationPreferenceNotFoundException(request.customerId()));

        Set<NotificationChannel> candidates = new LinkedHashSet<>();
        if (request.requestedChannel() != null && !request.requestedChannel().isBlank()) {
            candidates.add(NotificationChannel.valueOf(request.requestedChannel().toUpperCase()));
        }
        candidates.add(preference.getPreferredChannel());
        candidates.add(preference.getEscalationChannel());
        candidates.add(NotificationChannel.EMAIL);

        boolean quietHoursApplied = isInsideQuietHours(preference.getQuietHoursStart(), preference.getQuietHoursEnd(), LocalTime.now());

        for (NotificationChannel channel : candidates) {
            if (isAllowed(preference, channel, quietHoursApplied)) {
                String reason = "Selected " + channel + " for event " + request.eventType();
                if (quietHoursApplied && channel == NotificationChannel.EMAIL) {
                    reason = "Quiet hours active; downgraded to EMAIL fallback";
                }
                return new CommunicationResolutionResponse(request.customerId(), channel.name(), reason, quietHoursApplied);
            }
        }

        throw new CommunicationPreferenceValidationException(
                "No permitted communication channel available for customerId=" + request.customerId());
    }

    private boolean isInsideQuietHours(LocalTime start, LocalTime end, LocalTime now) {
        if (start.equals(end)) {
            return false;
        }
        if (start.isBefore(end)) {
            return !now.isBefore(start) && now.isBefore(end);
        }
        return !now.isBefore(start) || now.isBefore(end);
    }

    private boolean isAllowed(CommunicationPreference preference, NotificationChannel channel, boolean quietHoursApplied) {
        if (quietHoursApplied && channel != NotificationChannel.EMAIL) {
            return false;
        }
        return switch (channel) {
            case SMS -> preference.isSmsOptIn();
            case EMAIL -> preference.isEmailOptIn();
            case VOICE -> preference.isVoiceOptIn();
        };
    }

    private CommunicationPreferenceResponse map(CommunicationPreference preference) {
        return new CommunicationPreferenceResponse(
                preference.getCustomerId(),
                preference.getPreferredChannel().name(),
                preference.isSmsOptIn(),
                preference.isEmailOptIn(),
                preference.isVoiceOptIn(),
                preference.getQuietHoursStart().toString(),
                preference.getQuietHoursEnd().toString(),
                preference.getEscalationChannel().name(),
                preference.getLanguageCode()
        );
    }

    private void validate(CommunicationPreferenceRequest request) {
        if (request.customerId() == null || request.customerId() <= 0) {
            throw new CommunicationPreferenceValidationException("customerId must be positive");
        }
        if (request.preferredChannel() == null || request.escalationChannel() == null) {
            throw new CommunicationPreferenceValidationException("preferredChannel and escalationChannel are required");
        }
        if (request.languageCode() == null || request.languageCode().isBlank()) {
            throw new CommunicationPreferenceValidationException("languageCode is required");
        }
    }
}