package com.vkm_backend.tournaments.infra.repository;

import com.vkm_backend.infra.global.specification.DynamicRepository;
import com.vkm_backend.tournaments.infra.entities.TournamentStagesEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;

public interface TournamentStageRepository extends DynamicRepository<TournamentStagesEntity, Long> {
    TournamentStagesEntity findByTournamentId(TournamentsEntity tournamentId);

    TournamentStagesEntity findByTournamentIdAndStageOrder(TournamentsEntity tournamentId, Integer stageOrder);
}
