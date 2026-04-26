package com.example.ivr.orchestrator.dto;

import java.math.BigDecimal;

public record PolicyResponse(
        Long id,
        Long customerId,
        String policyNumber,
        String policyType,
        String status,
        BigDecimal premiumAmount
) {
}
