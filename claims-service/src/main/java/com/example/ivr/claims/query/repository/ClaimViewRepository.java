package com.example.ivr.claims.query.repository;

import com.example.ivr.claims.query.entity.ClaimView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimViewRepository extends JpaRepository<ClaimView, String> {

    // existing queries (keep as-is)
    java.util.List<ClaimView> findByCustomerId(String customerId);

    // ✅ NEW — used for idempotency check in controller
    Optional<ClaimView> findByIdempotencyKey(String idempotencyKey);
}