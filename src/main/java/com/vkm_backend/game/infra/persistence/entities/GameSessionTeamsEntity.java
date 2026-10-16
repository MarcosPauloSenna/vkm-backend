package com.vkm_backend.game.infra.persistence.entities;

import com.vkm_backend.game.domain.GameSessionTeamStatus;
import com.vkm_backend.infra.audit.Auditable;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "game_session_teams")
public class GameSessionTeamsEntity extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_session_id", nullable = false)
    private GameSessionsEntity gameSessionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamsEntity teamId;

    @Column(name = "queue_position")
    private Integer queuePosition;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GameSessionTeamStatus status;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

}
