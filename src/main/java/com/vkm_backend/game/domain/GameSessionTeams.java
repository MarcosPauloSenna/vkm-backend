package com.vkm_backend.game.domain;

import com.vkm_backend.game.infra.persistence.entities.GameSessionsEntity;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameSessionTeams {

    private Long id;
    private GameSessionsEntity gameSessionId;
    private TeamsEntity teamId;
    private Integer queuePosition;
    private GameSessionTeamStatus status;
    private Instant joinedAt;
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
