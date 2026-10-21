package com.vkm_backend.tournaments.service;

import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.tournaments.domain.MatchupPair;
import com.vkm_backend.tournaments.domain.MatchupPreviewItem;
import com.vkm_backend.tournaments.domain.RoundPreviewItem;
import com.vkm_backend.tournaments.domain.TeamPreviewItem;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsTeamsEntity;
import com.vkm_backend.tournaments.infra.repository.TournamentsTeamsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RoundRobinMatchService {

private final TournamentsTeamsRepository tournamentsTeamsRepository;
private final RoundRobinMatchupGenerator roundRobinMatchupGenerator;

    public RoundRobinMatchService(TournamentsTeamsRepository tournamentsTeamsRepository, RoundRobinMatchupGenerator roundRobinMatchupGenerator) {
        this.tournamentsTeamsRepository = tournamentsTeamsRepository;
        this.roundRobinMatchupGenerator = roundRobinMatchupGenerator;
    }

    public List<RoundPreviewItem> generate(TournamentsEntity tournament, Random random) {
        List<TournamentsTeamsEntity> teams = tournamentsTeamsRepository.findByTournamentId(tournament);

        if (teams.size() < 2) {
            throw new BusinessException("Não há equipes suficientes para gerar confrontos.");
        }


        return roundRobinMatchupGenerator.generateRandom(teams, random);

    }

}
