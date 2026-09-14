package com.vkm_backend.user.infra.persistence;

import com.vkm_backend.user.dominio.UserId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    private  String name;

    private  LocalDate birthDate;

    @Column(name = "phone", unique = true, nullable = false)
    private  String phone;

    private  String profilePhoto;

    @Column(name = "username", nullable = false)
    private  String username;

    @Column(name = "password", nullable = false)
    private  String password;


    @Column(name = "active", nullable = false)
    private  int active;

    private  LocalTime lastLoginAt;

    @Column(name = "createdAt", nullable = false)
    private  LocalDateTime createdAt;

    @Column(name = "updatedAt", nullable = false)
    private  LocalDateTime updatedAt;

    public UserEntity(long id,
                      String name,
                      LocalDate birthDate,
                      String phone,
                      String profilePhoto,
                      String username,
                      String password,
                      int active,
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

    public UserEntity() {
    }
}
