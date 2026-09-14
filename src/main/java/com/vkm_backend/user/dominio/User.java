package com.vkm_backend.user.dominio;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class User {

    private final UserId id;
    private final String name;
    private final LocalDate birthDate;
    private final String phone;
    private final String profilePhoto;
    private final String username;
    private final String password;
    private final Integer active;
    private final LocalTime lastLoginAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public User(UserId id,
                String name,
                LocalDate birthDate,
                String phone,
                String profilePhoto,
                String username, String password,
                Integer active,
                LocalTime lastLoginAt,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.phone = phone;
        this.profilePhoto = profilePhoto;
        this.username = username;
        this.password = password;
        this.active = active;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
