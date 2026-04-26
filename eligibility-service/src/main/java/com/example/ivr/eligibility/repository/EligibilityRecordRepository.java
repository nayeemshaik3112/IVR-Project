package com.example.ivr.eligibility.repository;

import com.example.ivr.eligibility.entity.EligibilityRecord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EligibilityRecordRepository extends JpaRepository<EligibilityRecord, Long> {

    Optional<EligibilityRecord> findTopByCustomerIdAndPolicyNumberOrderByLastVerifiedAtDesc(Long customerId, String policyNumber);

    List<EligibilityRecord> findByCustomerIdOrderByLastVerifiedAtDesc(Long customerId);
}