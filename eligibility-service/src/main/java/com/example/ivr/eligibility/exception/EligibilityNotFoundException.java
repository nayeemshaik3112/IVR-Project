package com.example.ivr.eligibility.exception;

public class EligibilityNotFoundException extends RuntimeException {

    public EligibilityNotFoundException(Long customerId, String policyNumber) {
        super("No eligibility record found for customerId=%d policyNumber=%s".formatted(customerId, policyNumber));
    }
}