package com.vkm_backend.group.domain;

public enum GroupMemberStatus {
    APPROVED("approved"),
    PENDING("pending"),
    REJECTED("rejected"),
    SUSPENDED("supended"),
    CANCELLED("cancelled"),
    LEFT("left");

    private String status;

    GroupMemberStatus(String status) {
        this.status = status;
    }
}
