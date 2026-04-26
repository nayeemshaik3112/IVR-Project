package com.example.ivr.communication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalTime;

@Entity
@Table(name = "communication_preference")
public class CommunicationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel preferredChannel;

    @Column(nullable = false)
    private boolean smsOptIn;

    @Column(nullable = false)
    private boolean emailOptIn;

    @Column(nullable = false)
    private boolean voiceOptIn;

    @Column(nullable = false)
    private LocalTime quietHoursStart;

    @Column(nullable = false)
    private LocalTime quietHoursEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel escalationChannel;

    @Column(nullable = false)
    private String languageCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public NotificationChannel getPreferredChannel() {
        return preferredChannel;
    }

    public void setPreferredChannel(NotificationChannel preferredChannel) {
        this.preferredChannel = preferredChannel;
    }

    public boolean isSmsOptIn() {
        return smsOptIn;
    }

    public void setSmsOptIn(boolean smsOptIn) {
        this.smsOptIn = smsOptIn;
    }

    public boolean isEmailOptIn() {
        return emailOptIn;
    }

    public void setEmailOptIn(boolean emailOptIn) {
        this.emailOptIn = emailOptIn;
    }

    public boolean isVoiceOptIn() {
        return voiceOptIn;
    }

    public void setVoiceOptIn(boolean voiceOptIn) {
        this.voiceOptIn = voiceOptIn;
    }

    public LocalTime getQuietHoursStart() {
        return quietHoursStart;
    }

    public void setQuietHoursStart(LocalTime quietHoursStart) {
        this.quietHoursStart = quietHoursStart;
    }

    public LocalTime getQuietHoursEnd() {
        return quietHoursEnd;
    }

    public void setQuietHoursEnd(LocalTime quietHoursEnd) {
        this.quietHoursEnd = quietHoursEnd;
    }

    public NotificationChannel getEscalationChannel() {
        return escalationChannel;
    }

    public void setEscalationChannel(NotificationChannel escalationChannel) {
        this.escalationChannel = escalationChannel;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}