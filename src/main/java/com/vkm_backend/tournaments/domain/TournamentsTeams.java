package com.vkm_backend.tournaments.domain;

import com.vkm_backend.teams.domain.Teams;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class TournamentsTeams {

    private Long id;
    private Tournaments tournamentId;
    private Teams teamId;
    private Integer seed;
    private TournamentTeamsStatus status;
    private Instant createdAt;

    public TournamentsTeams(Instant createdAt,
                            Long id,
                            Integer seed,
                            TournamentTeamsStatus status,
                            Teams teamId,
                            Tournaments tournamentId) {
        this.createdAt = createdAt;
        this.id = id;
        this.seed = seed;
        this.status = status;
        this.teamId = teamId;
        this.tournamentId = tournamentId;
    }

    public TournamentsTeams() {
    }
}
