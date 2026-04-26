package com.example.ivr.claims.command.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CreateClaimCommand {

    @TargetAggregateIdentifier
    private final String claimId;
    private final Long customerId;
    private final String policyNumber;
    private final String description;

    public CreateClaimCommand(String claimId, Long customerId, String policyNumber, String description) {
        this.claimId = claimId;
        this.customerId = customerId;
        this.policyNumber = policyNumber;
        this.description = description;
    }

    public String getClaimId() {
        return claimId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public String getDescription() {
        return description;
    }
}
