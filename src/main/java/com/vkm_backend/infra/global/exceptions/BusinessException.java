package com.vkm_backend.infra.global.exceptions;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{


    public BusinessException(String message) {
        super(message);
    }
}
