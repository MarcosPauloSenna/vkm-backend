package com.vkm_backend.user.infra.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(@NotBlank(message = "refreshToken não informado")
                                  String refreshToken) {
}
