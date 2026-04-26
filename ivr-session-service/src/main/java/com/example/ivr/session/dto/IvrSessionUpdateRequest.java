package com.example.ivr.session.dto;

public record IvrSessionUpdateRequest(
        Long customerId,
        String selectedPolicyNumber,
        String lastSummary
) {
}