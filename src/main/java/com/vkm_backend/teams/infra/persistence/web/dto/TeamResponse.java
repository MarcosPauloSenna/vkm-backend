package com.vkm_backend.teams.infra.persistence.web.dto;

import com.vkm_backend.teams.domain.CompositionHash;

public record TeamResponse(Long id,
                           String groupName,
                           String teamName,
                           Integer teamSize,
                           CompositionHash compositionHash,
                           String createdAt) {
}
