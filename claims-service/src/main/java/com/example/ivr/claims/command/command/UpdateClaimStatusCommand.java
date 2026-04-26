package com.example.ivr.claims.command.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class UpdateClaimStatusCommand {

    @TargetAggregateIdentifier
    private final String claimId;
    private final String status;

    public UpdateClaimStatusCommand(String claimId, String status) {
        this.claimId = claimId;
        this.status = status;
    }

    public String getClaimId() {
        return claimId;
    }

    public String getStatus() {
        return status;
    }
}
