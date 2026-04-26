package com.example.ivr.eligibility.exception;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EligibilityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(EligibilityNotFoundException exception) {
        return new ApiError(Instant.now(), HttpStatus.NOT_FOUND.value(), "Not Found", exception.getMessage());
    }

    @ExceptionHandler(EligibilityValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(EligibilityValidationException exception) {
        return new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Bad Request", exception.getMessage());
    }
}