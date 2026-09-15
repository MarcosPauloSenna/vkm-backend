package com.vkm_backend.user.infra.web;

import com.vkm_backend.user.dominio.EnumRoleUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


public record CreateUserRequest( String name,
        LocalDate birthDate,
        String phone,
        String profilePhoto,
        String username,
        String password) {
}
