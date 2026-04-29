package com.example.ivr.claims.command.aggregate;

import com.example.ivr.claims.command.command.CreateClaimCommand;
import com.example.ivr.claims.command.command.UpdateClaimStatusCommand;
import com.example.ivr.claims.command.event.ClaimCreatedEvent;
import com.example.ivr.claims.command.event.ClaimStatusUpdatedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.Instant;

@Aggregate
@NoArgsConstructor
@Slf4j
public class ClaimAggregate {

    @AggregateIdentifier
    private String claimId;

    private String customerId;
    private String policyNumber;
    private String status;
    private String idempotencyKey;   // ✅ NEW FIELD

    // ─────────────────────────────────────────────────
    // COMMAND HANDLER — Create Claim
    // ─────────────────────────────────────────────────
    @CommandHandler
    public ClaimAggregate(CreateClaimCommand command) {
        log.info("Handling CreateClaimCommand: claimId={} idempotencyKey={}",
                command.getClaimId(), command.getIdempotencyKey());

        AggregateLifecycle.apply(ClaimCreatedEvent.builder()
                .claimId(command.getClaimId())
                .customerId(command.getCustomerId())
                .policyNumber(command.getPolicyNumber())
                .description(command.getDescription())
                .claimType(command.getClaimType())
                .amount(command.getAmount())
                .status("CREATED")
                .occurredAt(Instant.now())
                .idempotencyKey(command.getIdempotencyKey())  // ✅ pass through
                .build());
    }

    // ─────────────────────────────────────────────────
    // COMMAND HANDLER — Update Claim Status
    // (existing — do not change)
    // ─────────────────────────────────────────────────
    @CommandHandler
    public void handle(UpdateClaimStatusCommand command) {
        log.info("Handling UpdateClaimStatusCommand: claimId={} newStatus={}",
                command.getClaimId(), command.getStatus());

        AggregateLifecycle.apply(ClaimStatusUpdatedEvent.builder()
                .claimId(command.getClaimId())
                .status(command.getStatus())
                .occurredAt(Instant.now())
                .build());
    }

    // ─────────────────────────────────────────────────
    // EVENT SOURCING HANDLER — ClaimCreatedEvent
    // ─────────────────────────────────────────────────
    @EventSourcingHandler
    public void on(ClaimCreatedEvent event) {
        this.claimId = event.getClaimId();
        this.customerId = event.getCustomerId();
        this.policyNumber = event.getPolicyNumber();
        this.status = event.getStatus();
        this.idempotencyKey = event.getIdempotencyKey();  // ✅ store in aggregate state
    }

    // ─────────────────────────────────────────────────
    // EVENT SOURCING HANDLER — ClaimStatusUpdatedEvent
    // (existing — do not change)
    // ─────────────────────────────────────────────────
    @EventSourcingHandler
    public void on(ClaimStatusUpdatedEvent event) {
        this.status = event.getStatus();
    }
}