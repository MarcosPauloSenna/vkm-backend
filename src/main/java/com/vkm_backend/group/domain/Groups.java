package com.vkm_backend.group.domain;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Groups {

    private Long id;
    private String name;
    private String description;
    private String city;
    private String state;
    private GroupActive active;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;

    public Groups(GroupActive active, String city, LocalDateTime createdAt, Long createdBy, String description, Long id, String name, String state, LocalDateTime updatedAt, Long updatedBy) {
        this.active = active;
        this.city = city;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.description = description;
        this.id = id;
        this.name = name;
        this.state = state;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }


    public Groups() {
        this.active = GroupActive.ATIVO;
    }
}


