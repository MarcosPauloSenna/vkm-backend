package com.vkm_backend.user.infra.web.dto;


import java.time.LocalDate;

public record FindUserAdmResponse(
        Long id,
        String name,
        String username,
        String phone,
        LocalDate birthDate,
        Integer active){
}
