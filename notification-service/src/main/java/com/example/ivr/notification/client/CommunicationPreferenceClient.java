package com.example.ivr.notification.client;

import com.example.ivr.notification.dto.CommunicationResolutionResponse;
import com.example.ivr.notification.dto.NotificationResolutionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "communication-preference-service")
public interface CommunicationPreferenceClient {

    @PostMapping("/api/communication-preferences/resolve")
    CommunicationResolutionResponse resolve(@RequestBody NotificationResolutionRequest request);
}