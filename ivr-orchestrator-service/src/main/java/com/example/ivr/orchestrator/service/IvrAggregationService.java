package com.example.ivr.orchestrator.service;

import com.example.ivr.orchestrator.client.ClaimsClient;
import com.example.ivr.orchestrator.client.CustomerClient;
import com.example.ivr.orchestrator.client.EligibilityClient;
import com.example.ivr.orchestrator.client.IvrSessionClient;
import com.example.ivr.orchestrator.client.PolicyClient;
import com.example.ivr.orchestrator.dto.ClaimResponse;
import com.example.ivr.orchestrator.dto.CustomerResponse;
import com.example.ivr.orchestrator.dto.EligibilityResponse;
import com.example.ivr.orchestrator.dto.IvrAggregatedResponse;
import com.example.ivr.orchestrator.dto.IvrSessionStartRequest;
import com.example.ivr.orchestrator.dto.IvrSessionUpdateRequest;
import com.example.ivr.orchestrator.dto.PolicyResponse;
import com.example.ivr.orchestrator.entity.IvrRequestLog;
import com.example.ivr.orchestrator.repository.IvrRequestLogRepository;
import com.example.ivr.orchestrator.support.IvrRequestStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IvrAggregationService {

    private static final Logger log = LoggerFactory.getLogger(IvrAggregationService.class);

    private final CustomerClient customerClient;
    private final PolicyClient policyClient;
    private final ClaimsClient claimsClient;
    private final EligibilityClient eligibilityClient;
    private final IvrSessionClient ivrSessionClient;
    private final Executor ivrExecutor;
    private final IvrRequestLogRepository ivrRequestLogRepository;
    private final ObjectMapper objectMapper;
    private final Counter idempotentHitCounter;
    private final Counter requestFailureCounter;

    public IvrAggregationService(CustomerClient customerClient,
                                 PolicyClient policyClient,
                                 ClaimsClient claimsClient,
                                 EligibilityClient eligibilityClient,
                                 IvrSessionClient ivrSessionClient,
                                 Executor ivrExecutor,
                                 IvrRequestLogRepository ivrRequestLogRepository,
                                 ObjectMapper objectMapper,
                                 MeterRegistry meterRegistry) {
        this.customerClient = customerClient;
        this.policyClient = policyClient;
        this.claimsClient = claimsClient;
        this.eligibilityClient = eligibilityClient;
        this.ivrSessionClient = ivrSessionClient;
        this.ivrExecutor = ivrExecutor;
        this.ivrRequestLogRepository = ivrRequestLogRepository;
        this.objectMapper = objectMapper;
        this.idempotentHitCounter = meterRegistry.counter("ivr.requests.idempotent.hit");
        this.requestFailureCounter = meterRegistry.counter("ivr.requests.failed");
    }

    @CircuitBreaker(name = "ivrAggregator", fallbackMethod = "fallback")
    @Retry(name = "ivrAggregator")
    @RateLimiter(name = "ivrAggregator")
    @TimeLimiter(name = "ivrAggregator")
    public CompletableFuture<IvrAggregatedResponse> aggregate(String phone, String requestIdHeader) {
        String requestId = normalizeRequestId(requestIdHeader);
        Optional<IvrAggregatedResponse> cachedResponse = findCompletedResponse(requestId);
        if (cachedResponse.isPresent()) {
            idempotentHitCounter.increment();
            log.info("ivr_request_cache_hit requestId={} phone={}", requestId, phone);
            return CompletableFuture.completedFuture(cachedResponse.get());
        }

        registerInFlightRequest(requestId, phone);
        ivrSessionClient.open(new IvrSessionStartRequest(requestId, phone, "CLAIM_STATUS"));

        return CompletableFuture.supplyAsync(() -> {
            log.info("ivr_request_started requestId={} phone={}", requestId, phone);
            CustomerResponse customer = customerClient.getCustomerByPhone(phone);
            CompletableFuture<List<PolicyResponse>> policiesFuture =
                    CompletableFuture.supplyAsync(() -> policyClient.getPolicies(customer.id()), ivrExecutor);
            CompletableFuture<List<ClaimResponse>> claimsFuture =
                    CompletableFuture.supplyAsync(() -> claimsClient.getClaims(customer.id()), ivrExecutor);

            List<PolicyResponse> policies = policiesFuture.join();
            List<ClaimResponse> claims = claimsFuture.join();
            String selectedPolicyNumber = policies.isEmpty() ? null : policies.get(0).policyNumber();
            EligibilityResponse eligibility = selectedPolicyNumber == null
                    ? new EligibilityResponse(customer.id(), null, null, false, "UNKNOWN",
                    "No active policy available for eligibility lookup", null, null, null)
                    : eligibilityClient.verify(customer.id(), selectedPolicyNumber);

            String summary = buildSummary(customer, policies, claims, eligibility);
            ivrSessionClient.update(requestId, new IvrSessionUpdateRequest(customer.id(), selectedPolicyNumber, summary));

            IvrAggregatedResponse response = new IvrAggregatedResponse(
                    requestId,
                    customer,
                    policies,
                    claims,
                    eligibility,
                    summary
            );
            markCompleted(requestId, response);
            ivrSessionClient.complete(requestId, summary);
            log.info("ivr_request_completed requestId={} customerId={} policyCount={} claimCount={} eligible={}",
                    requestId, customer.id(), policies.size(), claims.size(), eligibility.eligible());
            return response;
        }, ivrExecutor);
    }

    private String buildSummary(CustomerResponse customer,
                                List<PolicyResponse> policies,
                                List<ClaimResponse> claims,
                                EligibilityResponse eligibility) {
        return "Customer %s %s has %d policies, %d claims, and eligibility status %s (%s)."
                .formatted(customer.firstName(), customer.lastName(), policies.size(), claims.size(),
                        eligibility.coverageStatus(), eligibility.reason());
    }

    public CompletableFuture<IvrAggregatedResponse> fallback(String phone, String requestIdHeader, Throwable throwable) {
        String requestId = normalizeRequestId(requestIdHeader);
        requestFailureCounter.increment();
        log.warn("ivr_request_degraded requestId={} phone={} reason={}", requestId, phone, throwable.toString());
        CustomerResponse fallbackCustomer = new CustomerResponse(null, "Unavailable", "Customer", phone, null);
        EligibilityResponse eligibility = new EligibilityResponse(null, null, null, false, "UNAVAILABLE",
                "Eligibility service unavailable", null, null, null);
        IvrAggregatedResponse response = new IvrAggregatedResponse(
                requestId,
                fallbackCustomer,
                Collections.emptyList(),
                Collections.emptyList(),
                eligibility,
                "IVR response degraded. Downstream systems are temporarily unavailable."
        );
        markFailed(requestId, response);
        try {
            ivrSessionClient.fail(requestId, response.summaryMessage());
        } catch (RuntimeException exception) {
            log.warn("ivr_session_fail_callback_failed requestId={} reason={}", requestId, exception.toString());
        }
        return CompletableFuture.completedFuture(response);
    }

    private String normalizeRequestId(String requestIdHeader) {
        return requestIdHeader == null || requestIdHeader.isBlank()
                ? "ivr-" + UUID.randomUUID()
                : requestIdHeader;
    }

    private Optional<IvrAggregatedResponse> findCompletedResponse(String requestId) {
        try {
            return ivrRequestLogRepository.findById(requestId)
                    .filter(log -> IvrRequestStatus.COMPLETED.equals(log.getStatus()))
                    .map(this::deserializePayload);
        } catch (RuntimeException exception) {
            log.warn("ivr_request_cache_lookup_failed requestId={} reason={}", requestId, exception.toString());
            return Optional.empty();
        }
    }

    @Transactional
    protected void registerInFlightRequest(String requestId, String phone) {
        try {
            if (ivrRequestLogRepository.existsById(requestId)) {
                return;
            }
            IvrRequestLog requestLog = new IvrRequestLog();
            requestLog.setRequestId(requestId);
            requestLog.setPhone(phone);
            requestLog.setStatus(IvrRequestStatus.IN_PROGRESS);
            requestLog.setResponsePayload("{}");
            ivrRequestLogRepository.save(requestLog);
        } catch (DataIntegrityViolationException ignored) {
            log.info("ivr_request_duplicate_inflight requestId={}", requestId);
        } catch (RuntimeException exception) {
            log.warn("ivr_request_registration_failed requestId={} phone={} reason={}",
                    requestId, phone, exception.toString());
        }
    }

    @Transactional
    protected void markCompleted(String requestId, IvrAggregatedResponse response) {
        try {
            ivrRequestLogRepository.findById(requestId).ifPresentOrElse(requestLog -> {
                requestLog.setStatus(IvrRequestStatus.COMPLETED);
                requestLog.setResponsePayload(serializePayload(response));
                ivrRequestLogRepository.save(requestLog);
            }, () -> {
                IvrRequestLog requestLog = new IvrRequestLog();
                requestLog.setRequestId(requestId);
                requestLog.setPhone(response.customer().phone());
                requestLog.setStatus(IvrRequestStatus.COMPLETED);
                requestLog.setResponsePayload(serializePayload(response));
                ivrRequestLogRepository.save(requestLog);
            });
        } catch (RuntimeException exception) {
            log.warn("ivr_request_completion_persist_failed requestId={} reason={}", requestId, exception.toString());
        }
    }

    @Transactional
    protected void markFailed(String requestId, IvrAggregatedResponse response) {
        try {
            ivrRequestLogRepository.findById(requestId).ifPresentOrElse(requestLog -> {
                requestLog.setStatus(IvrRequestStatus.FAILED);
                requestLog.setResponsePayload(serializePayload(response));
                ivrRequestLogRepository.save(requestLog);
            }, () -> {
                IvrRequestLog requestLog = new IvrRequestLog();
                requestLog.setRequestId(requestId);
                requestLog.setPhone(response.customer().phone());
                requestLog.setStatus(IvrRequestStatus.FAILED);
                requestLog.setResponsePayload(serializePayload(response));
                ivrRequestLogRepository.save(requestLog);
            });
        } catch (RuntimeException exception) {
            log.warn("ivr_request_failure_persist_failed requestId={} reason={}", requestId, exception.toString());
        }
    }

    private String serializePayload(IvrAggregatedResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize IVR response", exception);
        }
    }

    private IvrAggregatedResponse deserializePayload(IvrRequestLog requestLog) {
        try {
            return objectMapper.readValue(requestLog.getResponsePayload(), IvrAggregatedResponse.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to deserialize IVR response payload", exception);
        }
    }
}