package com.vkm_backend.user.infra.web.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(@NotBlank(message = "Usuario não informado!")
                                    String username,
                                    @NotBlank(message = "Senha não informada")
                                    String password) {
}
