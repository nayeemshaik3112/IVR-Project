package com.example.ivr.claims.query.projection;

import com.example.ivr.claims.command.event.ClaimCreatedEvent;
import com.example.ivr.claims.command.event.ClaimStatusUpdatedEvent;
import com.example.ivr.claims.query.entity.ClaimView;
import com.example.ivr.claims.query.repository.ClaimViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;  // ✅ FIX 4: added

@Component
@RequiredArgsConstructor
@Slf4j
public class ClaimProjection {

    private final ClaimViewRepository claimViewRepository;

    // ─────────────────────────────────────────────────
    // EVENT HANDLER — Build Read Model on Claim Created
    // ─────────────────────────────────────────────────
    @EventHandler
    @Transactional  // ✅ FIX 4: ensures DB save is in transaction
    public void on(ClaimCreatedEvent event) {
        log.info("Projecting ClaimCreatedEvent: claimId={}", event.getClaimId());

        ClaimView view = ClaimView.builder()
                .claimId(event.getClaimId())
                .customerId(event.getCustomerId())   // String → String ✅
                .policyNumber(event.getPolicyNumber())
                .description(event.getDescription())
                .claimType(event.getClaimType())
                .amount(event.getAmount())
                .status(event.getStatus())
                .createdAt(event.getOccurredAt())
                .updatedAt(event.getOccurredAt())
                .idempotencyKey(event.getIdempotencyKey())
                .build();

        claimViewRepository.save(view);
        log.info("ClaimView saved: claimId={}", event.getClaimId());
    }

    // ─────────────────────────────────────────────────
    // EVENT HANDLER — Update Read Model on Status Change
    // ─────────────────────────────────────────────────
    @EventHandler
    @Transactional  // ✅ FIX 4: ensures DB update is in transaction
    public void on(ClaimStatusUpdatedEvent event) {
        log.info("Projecting ClaimStatusUpdatedEvent: claimId={} status={}",
                event.getClaimId(), event.getStatus());

        claimViewRepository.findById(event.getClaimId()).ifPresent(view -> {
            view.setStatus(event.getStatus());
            view.setUpdatedAt(event.getOccurredAt());
            claimViewRepository.save(view);
        });
    }

    // ✅ FIX 1: REMOVED @QueryHandler methods
    // GetClaimByIdQuery and GetClaimsByCustomerIdQuery classes do NOT exist
    // Queries are handled directly by ClaimQueryService — no duplication needed
}