package com.example.ivr.eligibility.controller;

import com.example.ivr.eligibility.dto.EligibilityUpsertRequest;
import com.example.ivr.eligibility.dto.EligibilityVerificationResponse;
import com.example.ivr.eligibility.service.EligibilityService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/eligibility")
public class EligibilityController {

    private final EligibilityService eligibilityService;

    public EligibilityController(EligibilityService eligibilityService) {
        this.eligibilityService = eligibilityService;
    }

    @PostMapping
    public EligibilityVerificationResponse upsert(@RequestBody EligibilityUpsertRequest request) {
        return eligibilityService.upsert(request);
    }

    @GetMapping("/verify")
    public EligibilityVerificationResponse verify(
            @RequestParam Long customerId,
            @RequestParam String policyNumber,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate serviceDate) {
        return eligibilityService.verify(customerId, policyNumber, serviceDate == null ? LocalDate.now() : serviceDate);
    }

    @GetMapping("/customer/{customerId}")
    public List<EligibilityVerificationResponse> getByCustomer(@PathVariable Long customerId) {
        return eligibilityService.getByCustomerId(customerId);
    }
}