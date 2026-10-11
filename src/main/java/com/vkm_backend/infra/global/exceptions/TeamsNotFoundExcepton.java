package com.vkm_backend.infra.global.exceptions;

public class TeamsNotFoundExcepton extends RuntimeException{
    public TeamsNotFoundExcepton(String message) {
        super(message);
    }
}
