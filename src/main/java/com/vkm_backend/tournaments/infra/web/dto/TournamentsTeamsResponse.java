package com.vkm_backend.tournaments.infra.web.dto;

import com.vkm_backend.tournaments.domain.TournamentTeamsStatus;

import java.time.Instant;

public record TournamentsTeamsResponse(Long id,
                                       String nameTournament,
                                       String nameTeam,
                                       TournamentTeamsStatus status,
                                       Instant createdAt) {
}

