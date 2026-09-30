package com.vkm_backend.group.usecase;

import java.util.Map;

public final class GroupMemberSortFields {
    public GroupMemberSortFields() {
    }

    public static final Map<String, String> SORT_FIELDS = Map.of(
            "codigo","id",
             "nome","userId.name",
            "funcao","role",
            "status","status",
             "aprovadoEm", "approvedAt"
    );
}
