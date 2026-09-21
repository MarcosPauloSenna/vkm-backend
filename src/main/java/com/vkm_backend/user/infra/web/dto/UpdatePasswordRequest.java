package com.vkm_backend.user.infra.web.dto;

import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(@Size(min = 8, max = 27, message = "A Senha deve possuir no minimo 8 caracteres")
                                    String newPassword) {
}
