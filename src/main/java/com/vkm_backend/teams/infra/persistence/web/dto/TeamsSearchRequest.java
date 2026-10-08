package com.vkm_backend.teams.infra.persistence.web.dto;

import com.vkm_backend.teams.domain.MemberFilterMode;

import java.time.Instant;
import java.util.List;

public record TeamsSearchRequest(Long id,
                                 String name,
                                 List<Long> memberIds,
                                 Instant createdAtFrom,
                                 Instant createdAtTo,
                                 Integer size,
                                 MemberFilterMode memberFilterMode) {
}
