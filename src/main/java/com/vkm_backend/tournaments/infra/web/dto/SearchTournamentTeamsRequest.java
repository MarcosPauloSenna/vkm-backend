package com.vkm_backend.tournaments.infra.web.dto;

import com.vkm_backend.tournaments.domain.TournamentTeamsStatus;

import java.time.Instant;

public record SearchTournamentTeamsRequest(Long tournamentTeamsId,
                                           String nameTeam,
                                           Integer seed,
                                           String groupName,
                                           TournamentTeamsStatus status,
                                           Instant createdAtFrom,
                                           Instant createdAtTo) {
}
