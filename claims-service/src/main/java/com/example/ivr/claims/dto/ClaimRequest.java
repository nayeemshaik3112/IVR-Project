package com.example.ivr.claims.dto;

public record ClaimRequest(
        Long customerId,
        String policyNumber,
        String description
) {
}
