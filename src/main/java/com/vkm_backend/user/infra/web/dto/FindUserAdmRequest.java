package com.vkm_backend.user.infra.web.dto;


import java.time.LocalDate;

public record FindUserAdmRequest(
        Long id,
        String name,
        String username,
        LocalDate birthDate,
        Integer active){
}
