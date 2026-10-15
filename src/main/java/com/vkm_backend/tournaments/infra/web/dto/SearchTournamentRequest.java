package com.vkm_backend.tournaments.infra.web.dto;

import com.vkm_backend.tournaments.domain.TournamentsStatus;
import com.vkm_backend.tournaments.domain.TournamentsFormat;

import java.time.Instant;
import java.time.LocalDate;

public record SearchTournamentRequest(Long id,
                                      String name,
                                      String description,
                                      TournamentsStatus status,
                                      LocalDate startDateFrom,
                                      LocalDate startDateTo,
                                      LocalDate endDateFrom,
                                      LocalDate endDateTo,
                                      TournamentsFormat format,
                                      Instant createdAtFrom,
                                      Instant createdAtTo) {
}
