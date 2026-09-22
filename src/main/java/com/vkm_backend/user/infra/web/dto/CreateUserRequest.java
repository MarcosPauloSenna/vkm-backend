package com.vkm_backend.user.infra.web.dto;



import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;


import java.time.LocalDate;



@Schema(description = "Dados necessários para criar um novo usuário")
public record CreateUserRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve possuir no máximo 100 caracteres")
        @Schema(description = "Nome completo do usuário", example = "João da Silva", maxLength = 100)
        String name,

        @JsonFormat(pattern = "dd/MM/yyyy")
        @NotNull(message = "Data de nascimento é obrigatório")
        @Schema(description = "Data de nascimento no formato dd/MM/yyyy", example = "15/08/1995", type = "string")
        LocalDate birthDate,

        @NotBlank(message = "Telefone é obrigatório")
        @Size(max = 11, min = 10, message = "O telefone deve possuir 11 caracteres" +
                " no formato DDD+9+NUMERO")
        @Schema(description = "Telefone com DDD, sem pontuação", example = "11987654321", minLength = 10, maxLength = 11)
        String phone,


        @Schema(description = "URL opcional da foto de perfil", example = "https://cdn.exemplo.com/perfil/joao.jpg")
        String profilePhoto,

        @NotBlank(message = "Nome de Usuario é obrigatório")
        @Size(max = 10, message = "Nome de Usuario deve possuir no máximo 10 caracteres")
        @Schema(description = "Nome de usuário único para login", example = "joaosilva", maxLength = 10)
        String username,

        @NotBlank(message = "A Senha é obrigatório")
        @Size(min = 8, max = 27, message = "A Senha deve possuir no minimo 8 caracteres")
        @Schema(description = "Senha do usuário; será armazenada apenas com hash", example = "SenhaSegura123", minLength = 8, maxLength = 27, format = "password")
        String password) {
}
