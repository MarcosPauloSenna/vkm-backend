package com.vkm_backend.tournaments.infra.web.dto;

import com.vkm_backend.tournaments.domain.TournamentsStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTournamentStatusRequest(@NotNull TournamentsStatus status) {
}
