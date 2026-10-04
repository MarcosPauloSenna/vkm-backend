package com.vkm_backend.user.usecase;

import java.util.Map;

public class UserSortFields {
    public UserSortFields() {
    }
    public static final Map<String, String> SORT_FIELDS = Map.of(
            "id", "id",
            "nome", "name",
            "usuario", "username",
            "telefone", "phone",
            "aniversario", "birthDate",
            "status", "active"
    );
}
