package com.vkm_backend.user.infra.web;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.time.LocalDate;



public record CreateUserRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve possuir no máximo 100 caracteres")
        String name,

        @NotNull(message = "Data de nascimento é obrigatório")
        LocalDate birthDate,

        @NotBlank(message = "Telefone é obrigatório")
        @Size(max = 11, min = 10, message = "O telefone deve possuir 11 caracteres" +
                " no formato DDD+9+NUMERO")
        String phone,


        String profilePhoto,

        @NotBlank(message = "Nome de Usuario é obrigatório")
        @Size(max = 10, message = "Nome de Usuario deve possuir no máximo 10 caracteres")
        String username,

        @NotBlank(message = "A Senha é obrigatório")
        @Size(max = 8, message = "A Senha deve possuir no máximo 8 caracteres")
        String password) {
}
