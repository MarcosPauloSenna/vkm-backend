package com.vkm_backend.user.infra.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token utilizado para encerrar a sessão do usuário")
public record LogoutRequest(String refreshToken) {
}
