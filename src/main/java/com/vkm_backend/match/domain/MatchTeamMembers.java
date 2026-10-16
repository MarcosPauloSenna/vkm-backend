package com.vkm_backend.match.domain;

import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.match.persistence.entities.MatchTeamsEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchTeamMembers {

    private Long id;
    private MatchTeamsEntity matchTeamId;
    private GroupMembersEntity groupMembersId;
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
