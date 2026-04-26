package com.example.ivr.claims.command.event;

import java.time.Instant;

public class ClaimStatusUpdatedEvent {

    private final String claimId;
    private final String status;
    private final Instant occurredAt;

    public ClaimStatusUpdatedEvent(String claimId, String status, Instant occurredAt) {
        this.claimId = claimId;
        this.status = status;
        this.occurredAt = occurredAt;
    }

    public String getClaimId() {
        return claimId;
    }

    public String getStatus() {
        return status;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
