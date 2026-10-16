package com.vkm_backend.game.domain;

import com.vkm_backend.game.infra.persistence.entities.GameDaysEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameSessions {

    private Long id;
    private GroupsEntity groupId;
    private TournamentsEntity tournamentId;
    private GameDaysEntity gameDayId;
    private String name;
    private String sessionType;
    private String status;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean winnerStays;
    private Boolean loserLeaves;
    private Integer maxPoints;
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
