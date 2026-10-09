package com.vkm_backend.tournaments.infra.entities;

import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.tournaments.domain.TournamentTeamsStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Table(name = "tournaments_teams")
@Entity
public class TournamentsTeamsEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_tournaments_teams_tournaments"))
    private TournamentsEntity tournamentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_tournaments_teams_teams"))
    private TeamsEntity teamId;

    private Integer seed;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TournamentTeamsStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public TournamentsTeamsEntity(Instant createdAt,
                                  Long id,
                                  Integer seed,
                                  TournamentTeamsStatus status,
                                  TeamsEntity teamId,
                                  TournamentsEntity tournamentId) {
        this.createdAt = createdAt;
        this.id = id;
        this.seed = seed;
        this.status = status;
        this.teamId = teamId;
        this.tournamentId = tournamentId;
    }

    public TournamentsTeamsEntity() {
    }
}
