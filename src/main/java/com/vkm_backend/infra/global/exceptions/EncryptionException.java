package com.vkm_backend.infra.global.exceptions;

public class EncryptionException extends RuntimeException{

    public EncryptionException() {
        super("ENCRYPTION_EXCEPTION");
    }

    public EncryptionException(String message) {
        super(message);
    }
}
