package com.vkm_backend.teams.infra.persistence.web.dto;

public record TeamInfoResponse(FindOrCreateTeamResponse teamMembersResponse,
                               String message) {
}
