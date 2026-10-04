package com.vkm_backend.teams.infra.persistence.entities;


import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "team_members")
public class TeamMembersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teams_id", nullable = false ,foreignKey = @ForeignKey(name = "fk_team_members_teams"))
    private TeamsEntity teamsId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_members_id", nullable = false, foreignKey = @ForeignKey(name = "fk_team_members_group_members"))
    private GroupMembersEntity groupMembersId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public TeamMembersEntity(Instant createdAt, GroupMembersEntity groupMembersId, Long id, TeamsEntity teamsId) {
        this.createdAt = createdAt;
        this.groupMembersId = groupMembersId;
        this.id = id;
        this.teamsId = teamsId;
    }

    public TeamMembersEntity() {
    }
}
