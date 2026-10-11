package com.vkm_backend.tournaments.infra.web.dto;

import com.vkm_backend.tournaments.domain.TourrnamentsFormat;

import java.time.LocalDate;

public record CreateTournamentRequest(String name,
                                      String description,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      TourrnamentsFormat format) {
}
