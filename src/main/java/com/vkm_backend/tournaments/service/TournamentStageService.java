package com.vkm_backend.tournaments.service;


import com.vkm_backend.tournaments.infra.entities.TournamentStagesEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import com.vkm_backend.tournaments.infra.repository.TournamentStageRepository;
import com.vkm_backend.tournaments.infra.repository.TournamentsTeamsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TournamentStageService {

    private final TournamentStageRepository stageRepository;
    private final TournamentStageFactory stageFactory;
    private final TournamentsTeamsRepository tournamentTeamRepository;

    public TournamentStageService(
            TournamentStageRepository stageRepository,
            TournamentStageFactory stageFactory, TournamentsTeamsRepository tournamentTeamRepository) {
        this.stageRepository = stageRepository;
        this.stageFactory = stageFactory;
        this.tournamentTeamRepository = tournamentTeamRepository;
    }


    public void createInitialStages(
            TournamentsEntity tournament) {

        int teamCount = tournamentTeamRepository
                .countByTournamentId(tournament);

        var definitions =
                stageFactory.createInitialStage(tournament.getFormat(), teamCount);

        TournamentStagesEntity stages = new TournamentStagesEntity();
        stages.setName(definitions.name());
        stages.setStageOrder(definitions.stageOrder());
        stages.setStageType(definitions.stageType());
        stages.setTournamentId(tournament);
        stages.setCreatedAt(Instant.now());

        stageRepository.save(stages);
    }
}