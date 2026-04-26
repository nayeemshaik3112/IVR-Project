package com.example.ivr.communication.controller;

import com.example.ivr.communication.dto.CommunicationPreferenceRequest;
import com.example.ivr.communication.dto.CommunicationPreferenceResponse;
import com.example.ivr.communication.dto.CommunicationResolutionResponse;
import com.example.ivr.communication.dto.NotificationResolutionRequest;
import com.example.ivr.communication.service.CommunicationPreferenceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/communication-preferences")
public class CommunicationPreferenceController {

    private final CommunicationPreferenceService service;

    public CommunicationPreferenceController(CommunicationPreferenceService service) {
        this.service = service;
    }

    @PutMapping
    public CommunicationPreferenceResponse upsert(@RequestBody CommunicationPreferenceRequest request) {
        return service.upsert(request);
    }

    @GetMapping("/{customerId}")
    public CommunicationPreferenceResponse get(@PathVariable Long customerId) {
        return service.get(customerId);
    }

    @PostMapping("/resolve")
    public CommunicationResolutionResponse resolve(@RequestBody NotificationResolutionRequest request) {
        return service.resolve(request);
    }
}