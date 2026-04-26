package com.example.ivr.session.controller;

import com.example.ivr.session.dto.IvrSessionResponse;
import com.example.ivr.session.dto.IvrSessionStartRequest;
import com.example.ivr.session.dto.IvrSessionUpdateRequest;
import com.example.ivr.session.service.IvrSessionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ivr-sessions")
public class IvrSessionController {

    private final IvrSessionService service;

    public IvrSessionController(IvrSessionService service) {
        this.service = service;
    }

    @PostMapping
    public IvrSessionResponse open(@RequestBody IvrSessionStartRequest request) {
        return service.openOrResume(request);
    }

    @PutMapping("/{requestId}")
    public IvrSessionResponse update(@PathVariable String requestId, @RequestBody IvrSessionUpdateRequest request) {
        return service.update(requestId, request);
    }

    @PostMapping("/{requestId}/complete")
    public IvrSessionResponse complete(@PathVariable String requestId, @RequestParam String summary) {
        return service.complete(requestId, summary);
    }

    @PostMapping("/{requestId}/fail")
    public IvrSessionResponse fail(@PathVariable String requestId, @RequestParam String summary) {
        return service.fail(requestId, summary);
    }

    @GetMapping("/{requestId}")
    public IvrSessionResponse get(@PathVariable String requestId) {
        return service.get(requestId);
    }
}