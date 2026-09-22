package com.vkm_backend.user.infra.persistence;

import com.vkm_backend.user.domain.EnumRoleUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private  String name;

    private  LocalDate birthDate;

    @Column(name = "phone", unique = true, nullable = false)
    private  String phone;

    private  String profilePhoto;

    @Column(name = "username", unique = true, nullable = false)
    private  String username;

    @Column(name = "password", nullable = false)
    private  String password;


    @Column(name = "active", nullable = false)
    private  int active;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private EnumRoleUser role;

    private  LocalDateTime lastLoginAt;

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false)
    private  LocalDateTime createdAt;

    @UpdateTimestamp
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
                      LocalDateTime lastLoginAt,
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role.equals(EnumRoleUser.ADMIN)) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER"));
        else
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));


    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
       
        return  this.active == 1;
    }
}
