package com.vkm_backend.group.infra.persistence.entities;

import com.vkm_backend.group.domain.GroupActive;
import com.vkm_backend.infra.audit.Auditable;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
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
    @Enumerated(EnumType.ORDINAL)
    private GroupActive active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_groups_user"))
    private UserEntity owner;

    public GroupsEntity(Long id, GroupActive active, String city, String description, String name, String state, UserEntity owner) {
        this.id = id;
        this.active = active;
        this.city = city;
        this.description = description;
        this.name = name;
        this.state = state;
        this.owner = owner;
    }

    public GroupsEntity() {

    }
}
