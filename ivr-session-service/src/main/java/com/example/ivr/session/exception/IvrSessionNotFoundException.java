package com.example.ivr.session.exception;

public class IvrSessionNotFoundException extends RuntimeException {

    public IvrSessionNotFoundException(String requestId) {
        super("No IVR session found for requestId=" + requestId);
    }
}