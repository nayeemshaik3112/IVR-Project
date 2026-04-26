package com.example.ivr.policy.dto;

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
