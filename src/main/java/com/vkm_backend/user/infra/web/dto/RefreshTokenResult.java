package com.vkm_backend.user.infra.web.dto;

public record RefreshTokenResult(String accessToken,
                                 String refreshToken) {
}
