package com.vkm_backend.tournaments.infra.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegisterTournamentsTeamsRequest(@NotNull @Positive Long teamId) {
}
