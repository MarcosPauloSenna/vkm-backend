package com.vkm_backend.teams.domain;

import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class TeamMembers {
    private Long id;
    private TeamsEntity teamsId;
    private GroupMembersEntity groupMembersId;
    private Instant createdAt;

    public TeamMembers(Instant createdAt, GroupMembersEntity groupMembersId, Long id, TeamsEntity teamsId) {
        this.createdAt = createdAt;
        this.groupMembersId = groupMembersId;
        this.id = id;
        this.teamsId = teamsId;
    }

    public TeamMembers() {

    }
}
