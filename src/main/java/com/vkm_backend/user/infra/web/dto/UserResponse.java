package com.vkm_backend.user.infra.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Setter;

import java.time.LocalDate;


public record UserResponse(
        Long id,
        String name,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate birthDate,
        String phone,
        String username,
        String profilePhoto) {
}
