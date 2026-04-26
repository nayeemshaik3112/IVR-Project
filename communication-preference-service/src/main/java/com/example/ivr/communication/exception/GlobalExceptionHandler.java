package com.example.ivr.communication.exception;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CommunicationPreferenceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(CommunicationPreferenceNotFoundException exception) {
        return new ApiError(Instant.now(), HttpStatus.NOT_FOUND.value(), "Not Found", exception.getMessage());
    }

    @ExceptionHandler(CommunicationPreferenceValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(CommunicationPreferenceValidationException exception) {
        return new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Bad Request", exception.getMessage());
    }
}