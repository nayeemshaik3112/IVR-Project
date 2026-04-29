package com.example.ivr.claims.query.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "claim_view",
        indexes = {
                // ✅ NEW — unique index on idempotency_key to prevent duplicates at DB level
                @Index(name = "uk_idempotency_key", columnList = "idempotency_key", unique = true)
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimView {

    @Id
    @Column(name = "claim_id")
    private String claimId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "policy_number")
    private String policyNumber;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "claim_type")
    private String claimType;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // ✅ NEW COLUMN — stores the idempotency key from client
    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;
}