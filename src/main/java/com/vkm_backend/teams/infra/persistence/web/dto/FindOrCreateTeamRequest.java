package com.vkm_backend.teams.infra.persistence.web.dto;

import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collection;

public record FindOrCreateTeamRequest(@PathVariable Long groupId,
                                      String name,
                                      Collection<Long> memberIds) {
}
