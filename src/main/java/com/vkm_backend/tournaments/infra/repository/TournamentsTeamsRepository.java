package com.vkm_backend.tournaments.infra.repository;

import com.vkm_backend.infra.global.specification.DynamicRepository;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsTeamsEntity;

import java.util.List;

public interface TournamentsTeamsRepository extends DynamicRepository<TournamentsTeamsEntity, Long> {


    TournamentsTeamsEntity findByTeamIdAndTournamentId(TeamsEntity teamId, TournamentsEntity tournament);

    boolean existsByTeamIdAndTournamentId(TeamsEntity team, TournamentsEntity tournament);

    int countByTournamentId(TournamentsEntity id);

    List<TournamentsTeamsEntity> findByTournamentId(TournamentsEntity tournament);

    TournamentsTeamsEntity findByIdAndTournamentId(Long aLong, TournamentsEntity tournament);
}
