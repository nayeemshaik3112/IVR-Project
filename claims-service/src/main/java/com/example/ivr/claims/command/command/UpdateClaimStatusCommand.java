package com.example.ivr.claims.command.command;

import lombok.Builder;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Builder
public class UpdateClaimStatusCommand {

    @TargetAggregateIdentifier
    private final String claimId;
    private final String status;
}