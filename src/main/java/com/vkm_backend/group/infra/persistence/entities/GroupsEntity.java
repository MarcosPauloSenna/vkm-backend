package com.vkm_backend.group.infra.persistence;

import com.vkm_backend.infra.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "groups")
public class GroupsEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
    private String description;
    private String city;
    private String state;
    private int active;

    public GroupsEntity(Long id, int active, String city, String description, String name, String state) {
        this.id = id;
        this.active = active;
        this.city = city;
        this.description = description;
        this.name = name;
        this.state = state;
    }

    public GroupsEntity() {

    }
}
