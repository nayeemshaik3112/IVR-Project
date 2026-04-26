package com.example.ivr.eligibility.dto;

import java.math.BigDecimal;

public record EligibilityVerificationResponse(
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