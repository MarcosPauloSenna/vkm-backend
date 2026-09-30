package com.vkm_backend.group.usecase;

import java.util.Map;

public final class GroupSortFields {
    private GroupSortFields() {}

    public static final Map<String, String> SORT_FIELDS = Map.of(
            "id", "id",
            "nome", "name",
            "cidade", "city",
            "estado", "state",
            "status", "status",
            "criadoEm", "createdAt"
    );
}
