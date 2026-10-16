package com.vkm_backend.game.domain;

import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameDays {

    private Long id;
    private GroupsEntity groupId;
    private TournamentsEntity tournamentId;
    private LocalDate gameDate;
    private String location;
    private GameDaysStatus status;
    private String notes;
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
