package com.vkm_backend.tournaments.infra.web.dto;

import com.vkm_backend.tournaments.domain.TournamentsFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateTournamentRequest(@NotBlank String name,
                                      String description,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      @NotNull TournamentsFormat format) {
}
