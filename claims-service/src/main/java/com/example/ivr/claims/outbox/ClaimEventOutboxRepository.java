package com.example.ivr.claims.outbox;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;

public interface ClaimEventOutboxRepository extends JpaRepository<ClaimEventOutbox, Long> {

    Optional<ClaimEventOutbox> findByEventId(String eventId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    //this is jpql not sql, works with entity fields not with table columns
    @Query("""
            select outbox
            from ClaimEventOutbox outbox
            where outbox.status in :statuses
              and outbox.nextAttemptAt <= :now
            order by outbox.createdAt asc
            """)
        List<ClaimEventOutbox> findBatchForDispatch(Collection<ClaimEventOutboxStatus> statuses, Instant now, Pageable pageable);
}
