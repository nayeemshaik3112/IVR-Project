package com.example.ivr.orchestrator.dto;

import java.time.Instant;

public record IvrSessionResponse(
        String requestId,
        String phone,
        Long customerId,
        String selectedPolicyNumber,
        String intent,
        String lastSummary,
        String status,
        Instant startedAt,
        Instant lastUpdatedAt,
        Instant endedAt
) {
}