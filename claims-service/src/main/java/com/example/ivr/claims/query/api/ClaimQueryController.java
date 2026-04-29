package com.example.ivr.claims.query.api;

import com.example.ivr.claims.dto.ClaimResponse;
import com.example.ivr.claims.query.service.ClaimQueryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/claims")
public class ClaimQueryController {

    private final ClaimQueryService claimQueryService;

    public ClaimQueryController(ClaimQueryService claimQueryService) {
        this.claimQueryService = claimQueryService;
    }

    // ✅ FIX 3: Changed Long → String (customerId is String throughout system)
    @GetMapping
    public List<ClaimResponse> getClaims(@RequestParam String customerId) {
        return claimQueryService.getClaimsByCustomerId(customerId);
    }

    @GetMapping("/{claimId}")
    public ClaimResponse getClaim(@PathVariable String claimId) {
        return claimQueryService.getClaim(claimId);
    }
}