package com.example.ivr.orchestrator.client;

import com.example.ivr.orchestrator.dto.PolicyResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "policy-service")
public interface PolicyClient {

    @GetMapping("/api/policies")
    List<PolicyResponse> getPolicies(@RequestParam("customerId") Long customerId);
}
