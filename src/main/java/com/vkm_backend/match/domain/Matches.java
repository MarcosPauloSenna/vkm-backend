package com.vkm_backend.match.domain;

import com.vkm_backend.game.infra.persistence.entities.GameDaysEntity;
import com.vkm_backend.game.infra.persistence.entities.GameSessionsEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.match.persistence.entities.MatchTeamsEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentStagesEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Matches {

    private Long id;
    private GameDaysEntity gameDayId;
    private TournamentsEntity tournamentId;
    private TournamentStagesEntity tournamentStageId;
    private GroupsEntity groupId;
    private GameSessionsEntity gameSessionId;
    private Integer matchNumber;
    private Integer sequenceNumber;
    private MatchStatus status;
    private MatchTeamsEntity winnerMatchTeamId;
    private Instant startedAt;
    private Instant finishedAt;
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
