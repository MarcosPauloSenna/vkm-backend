package com.vkm_backend.teams.domain;

import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Teams {
    private Long id;
    private GroupsEntity groupId;
    private String name;
    private Integer teamSize;
    private CompositionHash compositionHash;
    private Long createdAt;

    public Teams(CompositionHash compositionHash, Long createdAt, GroupsEntity groupId, Long id, String name, Integer teamSize) {
        this.compositionHash = compositionHash;
        this.createdAt = createdAt;
        this.groupId = groupId;
        this.id = id;
        this.name = name;
        this.teamSize = teamSize;
    }
}
