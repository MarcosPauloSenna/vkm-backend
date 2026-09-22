package com.vkm_backend.user.infra.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Dados opcionais para atualizar o perfil do usuário autenticado")
public record UpdateUserRequest(

        @Size(max = 100, message = "Nome deve possuir no máximo 100 caracteres")
        @Schema(description = "Novo nome completo", example = "João da Silva", maxLength = 100)
        String nome,

        @JsonFormat(pattern = "dd/MM/yyyy")
        @Schema(description = "Nova data de nascimento no formato dd/MM/yyyy", example = "15/08/1995", type = "string")
        LocalDate birthDate,

        @Size(max = 11, min = 10, message = "O telefone deve possuir 11 caracteres" +
                " no formato DDD+9+NUMERO")
        @Schema(description = "Novo telefone com DDD, sem pontuação", example = "11987654321", minLength = 10, maxLength = 11)
        String phone,

        @Schema(description = "Nova URL da foto de perfil", example = "https://cdn.exemplo.com/perfil/joao-atualizado.jpg")
        String profilePhoto) {
}
