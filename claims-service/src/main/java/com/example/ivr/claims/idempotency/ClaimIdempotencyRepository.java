package com.example.ivr.claims.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClaimIdempotencyRepository
        extends JpaRepository<ClaimIdempotencyRecord, Long> {

    Optional<ClaimIdempotencyRecord> findByIdempotencyKey(String idempotencyKey);
}