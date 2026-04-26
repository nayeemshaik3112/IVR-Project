package com.example.ivr.orchestrator.client;

import com.example.ivr.orchestrator.dto.EligibilityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "eligibility-service")
public interface EligibilityClient {

    @GetMapping("/api/eligibility/verify")
    EligibilityResponse verify(@RequestParam Long customerId,
                               @RequestParam String policyNumber);
}