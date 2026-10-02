package com.vkm_backend.infra.global.exceptions;

public class MemberNotFoundException extends RuntimeException{
    public MemberNotFoundException(String message) {
        super(message);
    }
}
