package com.vkm_backend.tournaments.infra.repository;

import com.vkm_backend.infra.global.specification.DynamicRepository;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;

public interface TournamentsRepository extends DynamicRepository<TournamentsEntity, Long> {
}
