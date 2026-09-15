package com.vkm_backend.user.infra.web;

import java.time.LocalDate;

public record UserResponse(String name,
                           LocalDate birthDate,
                           String phone,
                           String username) {
        }
