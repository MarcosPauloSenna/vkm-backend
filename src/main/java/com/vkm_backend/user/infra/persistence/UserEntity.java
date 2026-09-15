package com.vkm_backend.user.infra.persistence;

import com.vkm_backend.user.dominio.EnumRoleUser;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

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
    private Long id;

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


    @Column(name = "active", nullable = false, columnDefinition = "DEFAULT 1")
    private  int active;

    @Column(name = "role", nullable = false, columnDefinition = "DEFAULT 'USER'")
    private EnumRoleUser role;

    private  LocalTime lastLoginAt;

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false)
    private  LocalDateTime createdAt;

    @CreationTimestamp
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
                      EnumRoleUser role,
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
        this.role = role;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UserEntity() {
    }
}
