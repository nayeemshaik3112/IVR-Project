package com.example.ivr.claims.command.api;

import com.example.ivr.claims.command.command.CreateClaimCommand;
import com.example.ivr.claims.command.command.UpdateClaimStatusCommand;
import com.example.ivr.claims.idempotency.ClaimIdempotencyRecord;
import com.example.ivr.claims.idempotency.ClaimIdempotencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
@Slf4j
public class ClaimCommandController {

    private final CommandGateway commandGateway;
    private final ClaimIdempotencyRepository idempotencyRepository; // ✅ dedicated table

    @PostMapping
    @Transactional  // ✅ idempotency save + command dispatch in one transaction
    public ResponseEntity<?> createClaim(
            @RequestBody CreateClaimRequest request,
            @RequestHeader(value = "X-Idempotency-Key", required = false)
            String idempotencyKey) {

        // STEP 1: Require idempotency key — don't silently generate
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "X-Idempotency-Key header is required",
                            "message", "Provide a unique key per request to prevent duplicates"
                    ));
        }

        log.info("createClaim: customerId={} idempotencyKey={}",
                request.getCustomerId(), idempotencyKey);

        // STEP 2: Check dedicated idempotency table (synchronous, reliable)
        Optional<ClaimIdempotencyRecord> existing =
                idempotencyRepository.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            log.warn("Duplicate detected — idempotencyKey={} claimId={}",
                    idempotencyKey, existing.get().getClaimId());

            return ResponseEntity
                    .status(HttpStatus.OK)   // ✅ 200 for duplicate
                    .body(Map.of(
                            "claimId",   existing.get().getClaimId(),
                            "status",    "CREATED",
                            "message",   "Claim already exists for this request",
                            "duplicate", true
                    ));
        }

        // STEP 3: Generate claimId and save idempotency record FIRST
        String claimId = UUID.randomUUID().toString();

        // ✅ Save to idempotency table synchronously — before Axon command
        // This is the real guard — not ClaimView (eventual consistency)
        ClaimIdempotencyRecord record = ClaimIdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .claimId(claimId)
                .createdAt(Instant.now())
                .build();
        idempotencyRepository.save(record);

        // STEP 4: Dispatch command to Axon
        CreateClaimCommand command = CreateClaimCommand.builder()
                .claimId(claimId)
                .customerId(request.getCustomerId())
                .policyNumber(request.getPolicyNumber())
                .description(request.getDescription())
                .claimType(request.getClaimType())
                .amount(request.getAmount())
                .idempotencyKey(idempotencyKey)
                .build();

        commandGateway.sendAndWait(command);

        log.info("Claim created: claimId={} idempotencyKey={}", claimId, idempotencyKey);

        return ResponseEntity
                .status(HttpStatus.CREATED)   // ✅ 201 for new
                .body(Map.of(
                        "claimId",        claimId,
                        "status",         "CREATED",
                        "message",        "Claim created successfully",
                        "idempotencyKey", idempotencyKey,
                        "duplicate",      false
                ));
    }

    @PutMapping("/{claimId}/status")
    public ResponseEntity<?> updateClaimStatus(
            @PathVariable String claimId,
            @RequestBody UpdateClaimStatusRequest request) {

        log.info("updateClaimStatus: claimId={} newStatus={}", claimId, request.getStatus());

        commandGateway.sendAndWait(UpdateClaimStatusCommand.builder()
                .claimId(claimId)
                .status(request.getStatus())
                .build());

        return ResponseEntity.ok(Map.of(
                "claimId", claimId,
                "status",  request.getStatus(),
                "message", "Status updated successfully"
        ));
    }

    @lombok.Data
    public static class CreateClaimRequest {
        private String customerId;
        private String policyNumber;
        private String description;
        private String claimType;
        private Double amount;
    }

    @lombok.Data
    public static class UpdateClaimStatusRequest {
        private String status;
    }
}