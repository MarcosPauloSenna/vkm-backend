package com.vkm_backend.global.exceptions;

public class ValidationException extends RuntimeException{

    public ValidationException() {
        super("VALIDATION_EXCEPTION");
    }

    public ValidationException(String message) {
        super(message);
    }
}
