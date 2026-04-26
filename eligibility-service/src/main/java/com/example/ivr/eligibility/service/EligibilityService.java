package com.example.ivr.eligibility.service;

import com.example.ivr.eligibility.dto.EligibilityUpsertRequest;
import com.example.ivr.eligibility.dto.EligibilityVerificationResponse;
import com.example.ivr.eligibility.entity.CoverageStatus;
import com.example.ivr.eligibility.entity.EligibilityRecord;
import com.example.ivr.eligibility.exception.EligibilityNotFoundException;
import com.example.ivr.eligibility.exception.EligibilityValidationException;
import com.example.ivr.eligibility.repository.EligibilityRecordRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EligibilityService {

    private final EligibilityRecordRepository repository;

    public EligibilityService(EligibilityRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public EligibilityVerificationResponse upsert(EligibilityUpsertRequest request) {
        validateRequest(request);
        EligibilityRecord record = repository.findTopByCustomerIdAndPolicyNumberOrderByLastVerifiedAtDesc(
                request.customerId(), request.policyNumber()).orElseGet(EligibilityRecord::new);
        record.setCustomerId(request.customerId());
        record.setPolicyNumber(request.policyNumber());
        record.setPlanCode(request.planCode());
        record.setCoverageStatus(request.coverageStatus());
        record.setEffectiveDate(request.effectiveDate());
        record.setExpiryDate(request.expiryDate());
        record.setDeductibleRemaining(request.deductibleRemaining());
        record.setCopayPercentage(request.copayPercentage());
        record.setNetworkCode(request.networkCode());
        record.setLastVerifiedAt(Instant.now());
        repository.save(record);
        return verify(request.customerId(), request.policyNumber(), LocalDate.now());
    }

    @Transactional(readOnly = true)
    public EligibilityVerificationResponse verify(Long customerId, String policyNumber, LocalDate serviceDate) {
        EligibilityRecord record = repository.findTopByCustomerIdAndPolicyNumberOrderByLastVerifiedAtDesc(customerId, policyNumber)
                .orElseThrow(() -> new EligibilityNotFoundException(customerId, policyNumber));

        boolean eligible = true;
        String reason = "Coverage active for requested service date";

        if (record.getCoverageStatus() != CoverageStatus.ACTIVE) {
            eligible = false;
            reason = "Coverage is " + record.getCoverageStatus().name();
        } else if (serviceDate.isBefore(record.getEffectiveDate())) {
            eligible = false;
            reason = "Coverage not yet effective";
        } else if (serviceDate.isAfter(record.getExpiryDate())) {
            eligible = false;
            reason = "Coverage expired";
        }

        return new EligibilityVerificationResponse(
                record.getCustomerId(),
                record.getPolicyNumber(),
                record.getPlanCode(),
                eligible,
                record.getCoverageStatus().name(),
                reason,
                record.getDeductibleRemaining(),
                record.getCopayPercentage(),
                record.getNetworkCode()
        );
    }

    @Transactional(readOnly = true)
    public List<EligibilityVerificationResponse> getByCustomerId(Long customerId) {
        return repository.findByCustomerIdOrderByLastVerifiedAtDesc(customerId).stream()
                .map(record -> verify(record.getCustomerId(), record.getPolicyNumber(), LocalDate.now()))
                .toList();
    }

    private void validateRequest(EligibilityUpsertRequest request) {
        if (request.customerId() == null || request.customerId() <= 0) {
            throw new EligibilityValidationException("customerId must be positive");
        }
        if (request.policyNumber() == null || request.policyNumber().isBlank()) {
            throw new EligibilityValidationException("policyNumber is required");
        }
        if (request.planCode() == null || request.planCode().isBlank()) {
            throw new EligibilityValidationException("planCode is required");
        }
        if (request.coverageStatus() == null) {
            throw new EligibilityValidationException("coverageStatus is required");
        }
        if (request.effectiveDate() == null || request.expiryDate() == null
                || request.expiryDate().isBefore(request.effectiveDate())) {
            throw new EligibilityValidationException("effectiveDate/expiryDate are invalid");
        }
    }
}