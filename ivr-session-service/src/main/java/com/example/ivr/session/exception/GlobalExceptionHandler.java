package com.example.ivr.session.exception;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IvrSessionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(IvrSessionNotFoundException exception) {
        return new ApiError(Instant.now(), HttpStatus.NOT_FOUND.value(), "Not Found", exception.getMessage());
    }
}