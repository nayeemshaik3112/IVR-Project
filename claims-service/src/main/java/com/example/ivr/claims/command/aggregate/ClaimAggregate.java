package com.example.ivr.claims.command.aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import com.example.ivr.claims.command.command.CreateClaimCommand;
import com.example.ivr.claims.command.command.UpdateClaimStatusCommand;
import com.example.ivr.claims.command.event.ClaimCreatedEvent;
import com.example.ivr.claims.command.event.ClaimStatusUpdatedEvent;
import java.time.Instant;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class ClaimAggregate {

    @AggregateIdentifier
    private String claimId;
    private String status;

    public ClaimAggregate() {
    }

    @CommandHandler
    public ClaimAggregate(CreateClaimCommand command) {
        apply(new ClaimCreatedEvent(
                command.getClaimId(),
                command.getCustomerId(),
                command.getPolicyNumber(),
                command.getDescription(),
                "CREATED",
                Instant.now()
        ));
    }

    @CommandHandler
    public void handle(UpdateClaimStatusCommand command) {
        apply(new ClaimStatusUpdatedEvent(command.getClaimId(), command.getStatus(), Instant.now()));
    }

    @EventSourcingHandler
    public void on(ClaimCreatedEvent event) {
        this.claimId = event.getClaimId();
        this.status = event.getStatus();
    }

    @EventSourcingHandler
    public void on(ClaimStatusUpdatedEvent event) {
        this.status = event.getStatus();
    }
}
