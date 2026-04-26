package com.example.ivr.orchestrator.dto;

import java.util.List;

public record IvrAggregatedResponse(
        String requestId,
        CustomerResponse customer,
        List<PolicyResponse> policies,
        List<ClaimResponse> claims,
        EligibilityResponse eligibility,
        String summaryMessage
) {
}