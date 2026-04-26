package com.example.ivr.orchestrator.dto;

import java.time.Instant;

public record ClaimResponse(
        String claimId,
        Long customerId,
        String policyNumber,
        String status,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
}
