package com.vkm_backend.match.persistence.entities;

import com.vkm_backend.game.infra.persistence.entities.GameDaysEntity;
import com.vkm_backend.game.infra.persistence.entities.GameSessionsEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.infra.audit.Auditable;
import com.vkm_backend.match.domain.MatchStatus;
import com.vkm_backend.tournaments.infra.entities.TournamentStagesEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "matches")
public class MatchesEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_day_id")
    private GameDaysEntity gameDayId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private TournamentsEntity tournamentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_stage_id")
    private TournamentStagesEntity tournamentStageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupsEntity groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    private GameSessionsEntity gameSessionId;

    @Column(name = "match_number")
    private Integer matchNumber;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MatchStatus status = MatchStatus.SCHEDULED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_match_team_id")
    private MatchTeamsEntity winnerMatchTeamId;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    public MatchesEntity() {
    }
}
