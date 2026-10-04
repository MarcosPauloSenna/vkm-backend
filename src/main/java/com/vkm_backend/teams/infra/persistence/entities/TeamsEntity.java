package com.vkm_backend.teams.infra.persistence.entities;


import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.teams.domain.CompositionHash;
import com.vkm_backend.teams.service.CompositionHashConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "teams")
public class TeamsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_teams_groups"))
    private GroupsEntity groupId;

    @Column(name = "name", nullable = false)
    private String name;


    @Column(name = "team_size", nullable = false)
    private Integer teamSize;

    @Column(name = "composition_hash", nullable = false, length = 64)
    @Convert(converter = CompositionHashConverter.class)
    private CompositionHash compositionHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public TeamsEntity(CompositionHash compositionHash, Instant createdAt, GroupsEntity groupId, Long id, String name, Integer teamSize) {
        this.compositionHash = compositionHash;
        this.createdAt = createdAt;
        this.groupId = groupId;
        this.id = id;
        this.name = name;
        this.teamSize = teamSize;
    }

    public TeamsEntity() {
    }
}
