package com.example.ivr.orchestrator.client;

import com.example.ivr.orchestrator.dto.IvrSessionResponse;
import com.example.ivr.orchestrator.dto.IvrSessionStartRequest;
import com.example.ivr.orchestrator.dto.IvrSessionUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ivr-session-service")
public interface IvrSessionClient {

    @PostMapping("/api/ivr-sessions")
    IvrSessionResponse open(@RequestBody IvrSessionStartRequest request);

    @PutMapping("/api/ivr-sessions/{requestId}")
    IvrSessionResponse update(@PathVariable String requestId, @RequestBody IvrSessionUpdateRequest request);

    @PostMapping("/api/ivr-sessions/{requestId}/complete")
    IvrSessionResponse complete(@PathVariable String requestId, @RequestParam String summary);

    @PostMapping("/api/ivr-sessions/{requestId}/fail")
    IvrSessionResponse fail(@PathVariable String requestId, @RequestParam String summary);
}