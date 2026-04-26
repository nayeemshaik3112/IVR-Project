package com.example.ivr.claims.command.event;

import java.time.Instant;

public class ClaimCreatedEvent {

    private final String claimId;
    private final Long customerId;
    private final String policyNumber;
    private final String description;
    private final String status;
    private final Instant occurredAt;

    public ClaimCreatedEvent(String claimId,
                             Long customerId,
                             String policyNumber,
                             String description,
                             String status,
                             Instant occurredAt) {
        this.claimId = claimId;
        this.customerId = customerId;
        this.policyNumber = policyNumber;
        this.description = description;
        this.status = status;
        this.occurredAt = occurredAt;
    }

    public String getClaimId() {
        return claimId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
