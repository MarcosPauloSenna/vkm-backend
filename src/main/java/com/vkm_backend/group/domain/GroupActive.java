package com.vkm_backend.group.domain;

import lombok.Getter;

@Getter
public enum GroupActive {
    INACTIVE("inactive"), ACTIVE("active");

    private final String status;
    GroupActive(String status) {
        this.status = status;
    }
}
