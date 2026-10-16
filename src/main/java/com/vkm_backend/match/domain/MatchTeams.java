package com.vkm_backend.match.domain;

import com.vkm_backend.match.persistence.entities.MatchesEntity;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsTeamsEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchTeams {

    private Long id;
    private MatchesEntity matchId;
    private TeamsEntity teamId;
    private TournamentsTeamsEntity tournamentTeamId;
    private Integer position;
    private String name;
    private MatchSide side;
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
