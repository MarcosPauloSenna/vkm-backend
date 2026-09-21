package com.vkm_backend.user.infra.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UpdateUserRequest(

        @Size(max = 100, message = "Nome deve possuir no máximo 100 caracteres")
        String nome,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate birthDate,

        @Size(max = 11, min = 10, message = "O telefone deve possuir 11 caracteres" +
                " no formato DDD+9+NUMERO")
        String phone,

        String profilePhoto) {
}
