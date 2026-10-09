package com.vkm_backend.teams.infra.persistence.repository;

import com.vkm_backend.infra.global.specification.DynamicRepository;
import com.vkm_backend.teams.domain.CompositionHash;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;

public interface TeamsRepository extends DynamicRepository<TeamsEntity, Long> {

    TeamsEntity findByGroupId_IdAndCompositionHash(Long groupId, CompositionHash compositionHash);
}
