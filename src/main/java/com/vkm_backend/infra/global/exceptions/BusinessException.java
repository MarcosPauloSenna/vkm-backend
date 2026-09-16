package com.vkm_backend.infra.global.exceptions;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

    private int local;

    public BusinessException() {
        super("BUSINESS_EXCEPTION");
    }

    public BusinessException(String message, int local) {
        super(message);
        this.local = local;
    }


}
