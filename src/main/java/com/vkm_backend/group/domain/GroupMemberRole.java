package com.vkm_backend.group.domain;

public enum GroupMemberRole {
    OWNER("OWNER"), ADMIN("admin"), MEMBER("member");

    private String role;

    GroupMemberRole(String role) {
        this.role = role;
    }
}
