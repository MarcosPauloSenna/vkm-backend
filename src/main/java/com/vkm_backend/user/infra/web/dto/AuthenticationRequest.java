package com.vkm_backend.user.infra.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais utilizadas para autenticar um usuário")
public record AuthenticationRequest(@NotBlank(message = "Usuario não informado!")
                                    @Schema(description = "Nome de usuário cadastrado", example = "joao.silva")
                                    String username,
                                    @NotBlank(message = "Senha não informada")
                                    @Schema(description = "Senha do usuário", example = "SenhaSegura123")
                                    String password) {
}
