package com.vkm_backend.tournaments.infra.entities;

import com.vkm_backend.tournaments.domain.TournamentsStageTypes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Table(name = "tournament_stages")
@Entity
public class TournamentStagesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id", nullable = false)
    private TournamentsEntity tournamentId;

    private String name;

    @Column(name = "stage_type")
    @Enumerated(EnumType.STRING)
    private TournamentsStageTypes stageType;

    @Column(name = "stage_order")
    private Integer stageOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public TournamentStagesEntity(Instant createdAt,
                                  Long id,
                                  String name,
                                  Integer stageOrder,
                                  TournamentsStageTypes stageType,
                                  TournamentsEntity tournamentId) {
        this.createdAt = createdAt;
        this.id = id;
        this.name = name;
        this.stageOrder = stageOrder;
        this.stageType = stageType;
        this.tournamentId = tournamentId;
    }

    public TournamentStagesEntity() {
    }
}
