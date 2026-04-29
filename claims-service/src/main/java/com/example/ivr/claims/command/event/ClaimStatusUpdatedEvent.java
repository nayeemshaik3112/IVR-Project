package com.example.ivr.claims.command.event;

import lombok.Builder;
import lombok.Getter;
import java.time.Instant;

@Getter   // ✅ replaces manual getters
@Builder  // ✅ enables .builder() — fixes the error
public class ClaimStatusUpdatedEvent {

    private final String claimId;
    private final String status;
    private final Instant occurredAt;
}