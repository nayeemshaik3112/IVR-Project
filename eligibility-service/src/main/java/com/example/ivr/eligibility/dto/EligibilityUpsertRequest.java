package com.example.ivr.eligibility.dto;

import com.example.ivr.eligibility.entity.CoverageStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EligibilityUpsertRequest(
        Long customerId,
        String policyNumber,
        String planCode,
        CoverageStatus coverageStatus,
        LocalDate effectiveDate,
        LocalDate expiryDate,
        BigDecimal deductibleRemaining,
        BigDecimal copayPercentage,
        String networkCode
) {
}