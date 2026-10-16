package com.vkm_backend.match.persistence.entities;

import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.infra.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "match_team_members")
public class MatchTeamMembersEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_team_id", nullable = false)
    private MatchTeamsEntity matchTeamId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_members_id", nullable = false)
    private GroupMembersEntity groupMembersId;

    public MatchTeamMembersEntity() {
    }
}
