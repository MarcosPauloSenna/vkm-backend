package com.vkm_backend.user.infra.web.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token utilizado para renovar a sessão autenticada")
public record RefreshTokenRequest(@NotBlank(message = "refreshToken não informado")
                                  @Schema(description = "Refresh token retornado no login", example = "eyJhbGciOiJIUzI1NiJ9.refresh-token-exemplo")
                                  String refreshToken) {
}
