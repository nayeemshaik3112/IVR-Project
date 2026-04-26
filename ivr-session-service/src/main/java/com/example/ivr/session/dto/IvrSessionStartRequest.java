package com.example.ivr.session.dto;

public record IvrSessionStartRequest(
        String requestId,
        String phone,
        String intent
) {
}