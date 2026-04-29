package com.example.ivr.claims.exception;

public class DuplicateClaimException extends RuntimeException {

    private final String idempotencyKey;

    public DuplicateClaimException(String idempotencyKey) {
        super("Claim already exists for idempotency key: " + idempotencyKey);
        this.idempotencyKey = idempotencyKey;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}