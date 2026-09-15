package com.vkm_backend.user.dominio;

import lombok.Getter;

@Getter
public enum EnumRoleUser {
    ADMIN("admin"), USER("user");

    private String role;

    EnumRoleUser(String role) {
        this.role = role;
    }
}
