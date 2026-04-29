package com.example.ivr.claims.command.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimCreatedEvent {

    private String claimId;
    private String customerId;
    private String policyNumber;
    private String description;
    private String claimType;
    private Double amount;
    private String status;
    private Instant occurredAt;

    // ✅ NEW FIELD — carries idempotency key through to projection
    private String idempotencyKey;
}