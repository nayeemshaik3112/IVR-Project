package com.example.ivr.orchestrator.dto;

public record IvrSessionStartRequest(
        String requestId,
        String phone,
        String intent
) {
}