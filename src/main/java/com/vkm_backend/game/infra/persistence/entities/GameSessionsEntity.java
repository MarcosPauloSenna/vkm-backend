package com.vkm_backend.game.infra.persistence.entities;


import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.infra.audit.Auditable;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "game_sessions")
public class GameSessionsEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupsEntity groupId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id")
    private TournamentsEntity tournamentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_day_id", nullable = false)
    private GameDaysEntity gameDayId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "session_type", nullable = false)
    private String sessionType;

    @Column(name = "status", nullable = false)
    private String status;
    private LocalTime startTime;
    private LocalTime endTime;

    @Column(name = "winner_stays", nullable = false)
    private Boolean winnerStays;

    @Column(name = "loser_leaves", nullable = false)
    private Boolean loserLeaves;

    @Column(name = "max_points")
    private Integer maxPoints;

    public GameSessionsEntity(LocalTime endTime,
                              GameDaysEntity gameDayId,
                              GroupsEntity groupId,
                              Long id,
                              Boolean loserLeaves,
                              Integer maxPoints,
                              String name,
                              String sessionType,
                              LocalTime startTime,
                              String status,
                              TournamentsEntity tournamentId,
                              Boolean winnerStays) {
        this.endTime = endTime;
        this.gameDayId = gameDayId;
        this.groupId = groupId;
        this.id = id;
        this.loserLeaves = loserLeaves;
        this.maxPoints = maxPoints;
        this.name = name;
        this.sessionType = sessionType;
        this.startTime = startTime;
        this.status = status;
        this.tournamentId = tournamentId;
        this.winnerStays = winnerStays;
    }

    public GameSessionsEntity() {
    }
}
