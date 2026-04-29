package com.example.ivr.claims.command.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateClaimCommand {

    @TargetAggregateIdentifier
    private String claimId;

    private String customerId;
    private String policyNumber;
    private String description;
    private String claimType;
    private Double amount;

    // ✅ NEW FIELD — idempotency key from client
    private String idempotencyKey;
}