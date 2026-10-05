package com.vkm_backend.teams.infra.persistence.web.dto;

import com.vkm_backend.group.infra.persistence.web.dto.ListTeamMembers;
import com.vkm_backend.group.infra.persistence.web.dto.MemberStatusResponse;

import java.util.Collection;

public record FindOrCreateTeamResponse(Long id,
                                       String name,
                                       Integer teamSize,
                                       TeamMembersResponse memberNames) {
}
