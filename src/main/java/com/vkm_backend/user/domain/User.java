package com.vkm_backend.user.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Setter
@Getter
public class User {

    private Long id;
    private String name;
    private LocalDate birthDate;
    private String phone;
    private String profilePhoto;
    private String username;
    private String password;
    private Integer active;
    private EnumRoleUser role;
    private Instant lastLoginAt;
    private Instant createdAt;
    private Instant updatedAt;

    public User(Long id,
                String name,
                LocalDate birthDate,
                String phone,
                String profilePhoto,
                String username, String password,
                Integer active,
                EnumRoleUser role,
                Instant lastLoginAt,
                Instant createdAt,
                Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.phone = phone;
        this.profilePhoto = profilePhoto;
        this.username = username;
        this.password = password;
        this.active = active;
        this.role = role;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public User() {
        this.active = 1;
        this.role = EnumRoleUser.USER;
    }
}
