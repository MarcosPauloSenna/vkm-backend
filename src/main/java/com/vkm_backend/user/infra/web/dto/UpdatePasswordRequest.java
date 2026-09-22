package com.vkm_backend.user.infra.web.dto;

import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Nova senha do usuário autenticado")
public record UpdatePasswordRequest(@Size(min = 8, max = 27, message = "A Senha deve possuir no minimo 8 caracteres")
                                    @Schema(description = "Nova senha; será armazenada apenas com hash", example = "NovaSenha456", minLength = 8, maxLength = 27, format = "password")
                                    String newPassword) {
}
