package com.vkm_backend.match.domain;

import com.vkm_backend.match.persistence.entities.MatchTeamsEntity;
import com.vkm_backend.match.persistence.entities.MatchesEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchSets {

    private Long id;
    private MatchesEntity matchId;
    private Integer setNumber;
    private Integer homeScore;
    private Integer awayScore;
    private MatchTeamsEntity winnerMatchTeamId;
    private MatchSetStatus status;
    private Instant startedAt;
    private Instant finishedAt;
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
