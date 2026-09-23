package com.vkm_backend.infra.global.exceptions;

public class ForbiddenOperationException extends RuntimeException{

    public ForbiddenOperationException(String message) {
        super(message);
    }
}
