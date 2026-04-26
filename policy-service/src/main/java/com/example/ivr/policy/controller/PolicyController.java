package com.example.ivr.policy.controller;

import com.example.ivr.policy.dto.PolicyResponse;
import com.example.ivr.policy.service.PolicyService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping
    public List<PolicyResponse> getPolicies(@RequestParam Long customerId) {
        return policyService.getPolicies(customerId);
    }
}
