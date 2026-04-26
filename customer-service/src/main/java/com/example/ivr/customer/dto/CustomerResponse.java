package com.example.ivr.customer.dto;

public record CustomerResponse(
        Long id,
        String firstName,
        String lastName,
        String phone,
        String email
) {
}
