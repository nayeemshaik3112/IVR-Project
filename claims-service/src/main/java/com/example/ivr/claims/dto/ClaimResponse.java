package com.example.ivr.claims.dto;

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
