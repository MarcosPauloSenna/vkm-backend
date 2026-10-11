package com.vkm_backend.tournaments.infra.web.dto;

import com.vkm_backend.tournaments.domain.TournamentsStatus;
import com.vkm_backend.tournaments.domain.TourrnamentsFormat;

import java.time.Instant;
import java.time.LocalDate;

public record TournamentResponse(Long id,
                                 String name,
                                 String groupName,
                                 String description,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 TournamentsStatus status,
                                 TourrnamentsFormat format,
                                 Instant createdAt) {
}
