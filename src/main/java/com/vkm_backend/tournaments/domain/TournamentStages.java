package com.vkm_backend.tournaments.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class TournamentStages {

    private Long id;
    private Tournaments tournamentId;
    private String name;
    private TournamentsStageTypes stageType;
    private Integer stageOrder;
    private Instant createdAt;

    public TournamentStages(Instant createdAt,
                            Long id,
                            String name,
                            Integer stageOrder,
                            TournamentsStageTypes stageType,
                            Tournaments tournamentId) {
        this.createdAt = createdAt;
        this.id = id;
        this.name = name;
        this.stageOrder = stageOrder;
        this.stageType = stageType;
        this.tournamentId = tournamentId;
    }

    public TournamentStages() {
    }
}
