package com.vkm_backend.user.infra.web.dto;

public record AuthenticationResponse(String acessToken, String refreshToken) {
}
