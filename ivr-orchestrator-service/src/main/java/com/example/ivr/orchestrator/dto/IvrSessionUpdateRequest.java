package com.example.ivr.orchestrator.dto;

public record IvrSessionUpdateRequest(
        Long customerId,
        String selectedPolicyNumber,
        String lastSummary
) {
}