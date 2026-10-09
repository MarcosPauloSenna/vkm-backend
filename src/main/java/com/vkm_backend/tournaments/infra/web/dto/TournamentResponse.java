package com.vkm_backend.tournaments.infra.web.dto;

import com.vkm_backend.tournaments.domain.TournamentsStatus;
import com.vkm_backend.tournaments.domain.TourrnamentsFormat;

import java.time.Instant;

public record TournamentResponse(Long id,
                                 String name,
                                 String groupName,
                                 String description,
                                 String startDate,
                                 String endDate,
                                 TournamentsStatus status,
                                 TourrnamentsFormat format,
                                 Instant createdAt) {
}
