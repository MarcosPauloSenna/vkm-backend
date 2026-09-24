package com.vkm_backend.infra.global.specification.exception;

public class DynamicParamArgumentException extends IllegalArgumentException {
    public DynamicParamArgumentException(String message) {
        super(message);
    }
}
