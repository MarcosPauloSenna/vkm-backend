package com.vkm_backend.infra.security.exception;

import com.vkm_backend.infra.global.handler.ErrorResponse;

public record TokenValidationResult(
        Status status,
        String username,
        ErrorResponse errorResponse
) {

    public enum Status {
        VALID,
        EXPIRED,
        INVALID
    }

    public static TokenValidationResult valid(String username) {
        return new TokenValidationResult(
                Status.VALID,
                username,
                null
        );
    }

    public static TokenValidationResult expired(ErrorResponse errorResponse) {
        return new TokenValidationResult(
                Status.EXPIRED,
                null,
                errorResponse
        );
    }

    public static TokenValidationResult invalid(ErrorResponse errorResponse) {
        return new TokenValidationResult(
                Status.INVALID,
                null,
                errorResponse
        );
    }
}
