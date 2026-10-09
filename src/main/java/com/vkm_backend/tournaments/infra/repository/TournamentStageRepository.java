package com.vkm_backend.tournaments.infra.repository;

import com.vkm_backend.infra.global.specification.DynamicRepository;
import com.vkm_backend.tournaments.infra.entities.TournamentStagesEntity;

public interface TournamentStageRepository extends DynamicRepository<TournamentStagesEntity, Long> {
}
