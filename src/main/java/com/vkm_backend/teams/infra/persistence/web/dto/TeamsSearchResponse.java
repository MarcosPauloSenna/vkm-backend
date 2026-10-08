package com.vkm_backend.teams.infra.persistence.web.dto;

import com.vkm_backend.infra.global.dto.PageResponse;

public record TeamsSearchResponse(PageResponse<TeamResponse> response, String message) {
}
