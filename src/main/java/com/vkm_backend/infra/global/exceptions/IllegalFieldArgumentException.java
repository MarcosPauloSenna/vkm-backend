package com.vkm_backend.infra.global.exceptions;

public class IllegalFieldArgumentException extends RuntimeException{

    public IllegalFieldArgumentException(String message) {
        super(message);
    }
}
