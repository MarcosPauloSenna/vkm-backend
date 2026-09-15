package com.vkm_backend.user.infra.web;



import java.time.LocalDate;



public record CreateUserRequest( String name,
        LocalDate birthDate,
        String phone,
        String profilePhoto,
        String username,
        String password) {
}
