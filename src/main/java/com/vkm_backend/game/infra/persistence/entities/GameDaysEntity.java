package com.vkm_backend.game.infra.persistence.entities;

import com.vkm_backend.game.domain.GameDaysStatus;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.infra.audit.Auditable;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "game_days")
public class GameDaysEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupsEntity groupId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id")
    private TournamentsEntity tournamentId;

    @Column(name = "game_date", nullable = false)
    private LocalDate gameDate;
    private String location;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameDaysStatus status;
    private String notes;

    public GameDaysEntity(LocalDate gameDate,
                          GroupsEntity groupId,
                          Long id, String location,
                          String notes,
                          GameDaysStatus status,
                          TournamentsEntity tournamentId) {
        this.gameDate = gameDate;
        this.groupId = groupId;
        this.id = id;
        this.location = location;
        this.notes = notes;
        this.status = status;
        this.tournamentId = tournamentId;
    }

    public GameDaysEntity() {
    }
}
