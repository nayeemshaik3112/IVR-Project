package com.example.ivr.orchestrator.dto;

import java.math.BigDecimal;

public record EligibilityResponse(
        Long customerId,
        String policyNumber,
        String planCode,
        boolean eligible,
        String coverageStatus,
        String reason,
        BigDecimal deductibleRemaining,
        BigDecimal copayPercentage,
        String networkCode
) {
}