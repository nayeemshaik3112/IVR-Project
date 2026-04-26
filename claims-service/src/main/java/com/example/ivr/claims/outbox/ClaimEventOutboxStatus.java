package com.example.ivr.claims.outbox;

public enum ClaimEventOutboxStatus {
    PENDING,
    IN_PROGRESS,
    FAILED,
    PUBLISHED,
    DEAD_LETTER
}
