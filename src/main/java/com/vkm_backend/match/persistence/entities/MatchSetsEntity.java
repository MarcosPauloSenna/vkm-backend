package com.vkm_backend.match.persistence.entities;

import com.vkm_backend.infra.audit.Auditable;
import com.vkm_backend.match.domain.MatchSetStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "match_sets")
public class MatchSetsEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private MatchesEntity matchId;

    @Column(name = "set_number", nullable = false)
    private Integer setNumber;

    @Column(name = "home_score", nullable = false)
    private Integer homeScore = 0;

    @Column(name = "away_score", nullable = false)
    private Integer awayScore = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_match_team_id")
    private MatchTeamsEntity winnerMatchTeamId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MatchSetStatus status = MatchSetStatus.SCHEDULED;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    public MatchSetsEntity() {
    }
}
