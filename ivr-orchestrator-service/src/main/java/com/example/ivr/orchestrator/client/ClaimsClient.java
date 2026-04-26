package com.example.ivr.orchestrator.client;

import com.example.ivr.orchestrator.dto.ClaimResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "claims-service")
public interface ClaimsClient {

    @GetMapping("/api/claims")
    List<ClaimResponse> getClaims(@RequestParam("customerId") Long customerId);
}
