package com.vkm_backend.teams.infra.persistence.web.dto;

import com.vkm_backend.group.infra.persistence.web.dto.ListTeamMembers;

import java.time.Instant;
import java.util.Collection;

public record TeamMembersResponse(Long id, String teamsName, Collection<ListTeamMembers> groupMembersId, Instant createdAt) {
}
