package com.example.ivr.communication.exception;

public class CommunicationPreferenceNotFoundException extends RuntimeException {

    public CommunicationPreferenceNotFoundException(Long customerId) {
        super("No communication preference found for customerId=%d".formatted(customerId));
    }
}