package com.vkm_backend.group.domain;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Groups {

    private Long id;
    private String name;
    private String description;
    private String city;
    private String state;
    private int active;

    public Groups(Long id, int active, String city, String description, String name, String state) {
        this.id = id;
        this.active = active;
        this.city = city;
        this.description = description;
        this.name = name;
        this.state = state;
    }

    public Groups() {
        this.active = 1;
    }
}


