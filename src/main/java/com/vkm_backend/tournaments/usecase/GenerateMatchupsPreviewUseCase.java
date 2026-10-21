package com.vkm_backend.tournaments.usecase;


import com.vkm_backend.group.infra.persistence.web.dto.AccessPolicyResponse;
import com.vkm_backend.group.service.GroupAccessPolicy;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.TournamentNotFoundException;
import com.vkm_backend.tournaments.domain.*;
import com.vkm_backend.tournaments.infra.entities.TournamentStagesEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsTeamsEntity;
import com.vkm_backend.tournaments.infra.repository.TournamentStageRepository;
import com.vkm_backend.tournaments.infra.repository.TournamentsRepository;
import com.vkm_backend.tournaments.infra.repository.TournamentsTeamsRepository;
import com.vkm_backend.tournaments.infra.web.dto.ManualMatchupRequest;
import com.vkm_backend.tournaments.infra.web.dto.MatchupPreviewRequest;
import com.vkm_backend.tournaments.infra.web.dto.MatchupPreviewResponse;
import com.vkm_backend.tournaments.service.RoundRobinMatchService;
import com.vkm_backend.tournaments.service.ValidateRoundRobinMatchService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class GenerateMatchupsPreviewUseCase {
    private final GroupAccessPolicy groupAccessPolicy;
    private final TournamentsRepository tournamentsRepository;
    private final TournamentsTeamsRepository tournamentsTeamsRepository;
    private final RoundRobinMatchService roundRobinMatchService;
    private final TournamentStageRepository tournamentStageRepository;
    private final ValidateRoundRobinMatchService validateRoundRobinMatchService;


    public GenerateMatchupsPreviewUseCase(GroupAccessPolicy groupAccessPolicy, TournamentsRepository tournamentsRepository, TournamentsTeamsRepository tournamentsTeamsRepository, RoundRobinMatchService roundRobinMatchService, TournamentStageRepository tournamentStageRepository, ValidateRoundRobinMatchService validateRoundRobinMatchService) {
        this.groupAccessPolicy = groupAccessPolicy;
        this.tournamentsRepository = tournamentsRepository;
        this.tournamentsTeamsRepository = tournamentsTeamsRepository;
        this.roundRobinMatchService = roundRobinMatchService;
        this.tournamentStageRepository = tournamentStageRepository;
        this.validateRoundRobinMatchService = validateRoundRobinMatchService;
    }

    public MatchupPreviewResponse execute(MatchupPreviewRequest matchupPreviewRequest, Long tournamentId, Long groupId, String userName) {

        AccessPolicyResponse accessPolicyResponse = groupAccessPolicy.authorizeAdminAndOwner(groupId, userName);


        TournamentsEntity tournament = tournamentsRepository.findByIdAndGroupId(tournamentId, accessPolicyResponse.groups());
        if (tournament == null) {
            throw new TournamentNotFoundException("Torneio não encontrado.");
        }

        TournamentStagesEntity stage = tournamentStageRepository.findByTournamentIdAndStageOrder(tournament, 1);

        if (stage == null) {
            throw new BusinessException("Estágio do torneio não encontrado.");
        }

        List<TournamentsTeamsEntity> rotation = tournamentsTeamsRepository.findByTournamentId(tournament);

       int teamCount = rotation.size();

        if (teamCount < 2) {
            throw new BusinessException(
                    "O torneio precisa ter pelo menos duas equipes."
            );
        }

        int totalRounds = teamCount % 2 == 0
                ? teamCount - 1
                : teamCount;

        int matchesPerRound = teamCount / 2;

        Random random = new Random();


        if (matchupPreviewRequest.mode() == MatchupGenerationMode.RANDOM) {
            List<RoundPreviewItem> roundPreviewItems = generateRandom(tournament, random);
            return new MatchupPreviewResponse(tournament.getId(), stage.getId(), tournament.getFormat(), matchupPreviewRequest.mode(), roundPreviewItems.size(), totalRounds,roundPreviewItems);
        }

        List<ManualRoundRequest> validMatchups =
                validateRoundRobinMatchService.validateUniqueMatchups(
                        matchupPreviewRequest.rounds(),
                        rotation
                );


        Map<Long, TournamentsTeamsEntity> teamsById = rotation.stream()
                .collect(Collectors.toMap(
                        TournamentsTeamsEntity::getId,
                        Function.identity()
                ));


        List<RoundPreviewItem> rounds = new ArrayList<>();


        for (ManualRoundRequest roundRequest : matchupPreviewRequest.rounds()) {

            List<MatchupPreviewItem> matches = new ArrayList<>();
            Set<Long> teamsInRound = new HashSet<>();



            for (ManualMatchupRequest matchup : roundRequest.matchups()) {

                TournamentsTeamsEntity teamAEntity =
                        teamsById.get(matchup.teamAId());

                TournamentsTeamsEntity teamBEntity =
                        teamsById.get(matchup.teamBId());

                if (teamAEntity == null || teamBEntity == null) {
                    throw new BusinessException(
                            "Uma das equipes do confronto não pertence ao torneio."
                    );
                }

                if (!teamsInRound.add(teamAEntity.getId())
                        || !teamsInRound.add(teamBEntity.getId())) {
                    throw new BusinessException(
                            "Uma equipe não pode participar de mais de um confronto na mesma rodada."
                    );
                }

                TeamPreviewItem previewA = new TeamPreviewItem(
                        teamAEntity.getId(),
                        teamAEntity.getTeamId().getName()
                );

                TeamPreviewItem previewB = new TeamPreviewItem(
                        teamBEntity.getId(),
                        teamBEntity.getTeamId().getName()
                );

                matches.add(new MatchupPreviewItem(
                        matches.size() + 1,
                        previewA,
                        previewB
                ));
            }

            rounds.add(new RoundPreviewItem(
                    roundRequest.roundNumber(),
                    matches
            ));
        }


        return new MatchupPreviewResponse(tournament.getId(),
                stage.getId(),
                tournament.getFormat(),
                matchupPreviewRequest.mode(),
                rounds.size(),
                totalRounds,
                rounds);
    }


    private List<RoundPreviewItem> generateRandom(TournamentsEntity tournament, Random random) {

        List<RoundPreviewItem> roundPreviewItems = new ArrayList<>();

        if (Objects.requireNonNull(tournament.getFormat()) == TournamentsFormat.ROUND_ROBIN) {
            roundPreviewItems = roundRobinMatchService.generate(tournament, random);
        } else {
            throw new BusinessException("Formato de torneio não suportado para geração de confrontos.");
        }
        return roundPreviewItems;
    }

}
