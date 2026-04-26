package com.example.ivr.claims.outbox;

import com.example.ivr.claims.dto.ClaimEventMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClaimEventOutboxService {

    private static final Logger log = LoggerFactory.getLogger(ClaimEventOutboxService.class);

    private final ClaimEventOutboxRepository repository;
    private final ObjectMapper objectMapper;
    private final Counter storedCounter;
    private final Counter deadLetterCounter;
    private final int maxAttempts;
    private final Duration retryBaseDelay;
    private final Duration retryMaxDelay;

    public ClaimEventOutboxService(ClaimEventOutboxRepository repository,
                                   ObjectMapper objectMapper,
                                   MeterRegistry meterRegistry,
                                   @Value("${claims.outbox.max-attempts:8}") int maxAttempts,
                                   @Value("${claims.outbox.retry-base-delay:PT5S}") Duration retryBaseDelay,
                                   @Value("${claims.outbox.retry-max-delay:PT5M}") Duration retryMaxDelay) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.storedCounter = meterRegistry.counter("claims.outbox.stored");
        this.deadLetterCounter = meterRegistry.counter("claims.outbox.deadletter");
        this.maxAttempts = maxAttempts;
        this.retryBaseDelay = retryBaseDelay;
        this.retryMaxDelay = retryMaxDelay;
    }

    @Transactional
    public void store(ClaimEventMessage message) {
        if (repository.findByEventId(message.eventId()).isPresent()) {
            log.info("claim_outbox_duplicate_ignored eventId={} claimId={}", message.eventId(), message.claimId());
            return;
        }

        ClaimEventOutbox outbox = new ClaimEventOutbox();
        outbox.setEventId(message.eventId());
        outbox.setClaimId(message.claimId());
        outbox.setEventType(message.eventType());
        outbox.setPayload(serialize(message));
        outbox.setStatus(ClaimEventOutboxStatus.PENDING);
        outbox.setAttempts(0);
        outbox.setNextAttemptAt(Instant.now());
        try {
            repository.save(outbox);
            storedCounter.increment();
            log.info("claim_outbox_stored eventId={} claimId={} eventType={}",
                    message.eventId(), message.claimId(), message.eventType());
        } catch (DataIntegrityViolationException exception) {
            log.info("claim_outbox_store_failed eventId={} claimId={} reason={}", message.eventId(), message.claimId(), exception.getMostSpecificCause().getMessage());
            throw exception;
        }
    }

    //This will collect the rows of pending and failed and
    //lock them to publish into kafka
    @Transactional
    public List<ClaimEventOutbox> claimBatch(int batchSize) {
        List<ClaimEventOutbox> batch = repository.findBatchForDispatch(
                List.of(ClaimEventOutboxStatus.PENDING, ClaimEventOutboxStatus.FAILED),
                Instant.now(),
                PageRequest.of(0, batchSize)
        );
        batch.forEach(outbox -> outbox.setStatus(ClaimEventOutboxStatus.IN_PROGRESS));
        return batch;
    }

    @Transactional
    public void markPublished(Long outboxId) {
        repository.findById(outboxId).ifPresent(outbox -> {
            outbox.setStatus(ClaimEventOutboxStatus.PUBLISHED);
            outbox.setPublishedAt(Instant.now());
            outbox.setLastError(null);
        });
    }

    @Transactional
    public void markFailed(Long outboxId, String reason) {
        repository.findById(outboxId).ifPresent(outbox -> {
            int attempts = outbox.getAttempts() + 1;
            outbox.setAttempts(attempts);
            outbox.setLastError(truncate(reason));
            if (attempts >= maxAttempts) {
                outbox.setStatus(ClaimEventOutboxStatus.DEAD_LETTER);
                outbox.setNextAttemptAt(Instant.now());
                deadLetterCounter.increment();
                log.error("claim_outbox_dead_lettered eventId={} claimId={} attempts={} reason={}",
                        outbox.getEventId(), outbox.getClaimId(), attempts, truncate(reason));
                return;
            }
            outbox.setStatus(ClaimEventOutboxStatus.FAILED);
            outbox.setNextAttemptAt(Instant.now().plus(computeBackoff(attempts)));
            log.warn("claim_outbox_retry_scheduled eventId={} claimId={} attempts={} nextAttemptAt={} reason={}",
                    outbox.getEventId(), outbox.getClaimId(), attempts, outbox.getNextAttemptAt(), truncate(reason));
        });
    }

    public ClaimEventMessage deserialize(ClaimEventOutbox outbox) {
        try {
            return objectMapper.readValue(outbox.getPayload(), ClaimEventMessage.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to deserialize outbox payload " + outbox.getEventId(), exception);
        }
    }

    private String serialize(ClaimEventMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize outbox payload " + message.eventId(), exception);
        }
    }

    private Duration computeBackoff(int attempts) {
        long multiplier = 1L << Math.min(attempts - 1, 10);
        Duration computed = retryBaseDelay.multipliedBy(multiplier);
        return computed.compareTo(retryMaxDelay) > 0 ? retryMaxDelay : computed;
    }

    private String truncate(String input) {
        if (input == null) {
            return null;
        }
        return input.length() <= 1800 ? input : input.substring(0, 1800);
    }
}
