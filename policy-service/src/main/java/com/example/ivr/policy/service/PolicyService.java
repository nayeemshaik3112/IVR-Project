package com.example.ivr.policy.service;

import com.example.ivr.policy.dto.PolicyResponse;
import com.example.ivr.policy.repository.PolicyRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public List<PolicyResponse> getPolicies(Long customerId) {
        return policyRepository.findByCustomerId(customerId).stream()
                .map(policy -> new PolicyResponse(
                        policy.getId(),
                        policy.getCustomerId(),
                        policy.getPolicyNumber(),
                        policy.getPolicyType(),
                        policy.getStatus(),
                        policy.getPremiumAmount()
                ))
                .toList();
    }
}
