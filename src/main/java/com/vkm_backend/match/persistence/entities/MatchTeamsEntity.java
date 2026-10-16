package com.vkm_backend.match.persistence.entities;

import com.vkm_backend.infra.audit.Auditable;
import com.vkm_backend.match.domain.MatchSide;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsTeamsEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "match_teams")
public class MatchTeamsEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private MatchesEntity matchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private TeamsEntity teamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_team_id")
    private TournamentsTeamsEntity tournamentTeamId;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "side", nullable = false)
    @Enumerated(EnumType.STRING)
    private MatchSide side;

    public MatchTeamsEntity() {
    }
}
