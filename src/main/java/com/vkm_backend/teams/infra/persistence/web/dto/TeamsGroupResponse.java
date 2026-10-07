package com.vkm_backend.teams.infra.persistence.web.dto;

import java.time.Instant;

public record TeamsGroupResponse(
    Long id,
    String name,
    Integer size,
    Instant createdAt) {
}
