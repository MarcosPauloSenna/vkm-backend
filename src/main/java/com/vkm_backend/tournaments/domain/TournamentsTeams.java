package com.vkm_backend.tournaments.domain;

import com.vkm_backend.teams.domain.Teams;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class TournamentsTeams {

    private Long id;
    private TournamentsEntity tournamentId;
    private TeamsEntity teamId;
    private String groupName;
    private Integer seed;
    private TournamentTeamsStatus status;
    private Instant createdAt;

    public TournamentsTeams(Instant createdAt,
                            Long id,
                            Integer seed,
                            TournamentTeamsStatus status,
                            TeamsEntity teamId,
                            TournamentsEntity tournamentId,
                            String groupName) {
        this.createdAt = createdAt;
        this.id = id;
        this.seed = seed;
        this.status = status;
        this.teamId = teamId;
        this.groupName = groupName;
        this.tournamentId = tournamentId;
    }

    public TournamentsTeams() {
    }
}
