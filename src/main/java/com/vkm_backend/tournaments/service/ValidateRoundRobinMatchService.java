
package com.vkm_backend.tournaments.service;

import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.tournaments.domain.ManualRoundRequest;
import com.vkm_backend.tournaments.infra.entities.TournamentsTeamsEntity;
import com.vkm_backend.tournaments.infra.web.dto.ManualMatchupRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ValidateRoundRobinMatchService {

    public List<ManualRoundRequest> validateUniqueMatchups(
            List<ManualRoundRequest> rounds,
            List<TournamentsTeamsEntity> teams) {

        if (teams == null || teams.size() < 2) {
            throw new BusinessException(
                    "É necessário ter pelo menos duas equipes inscritas."
            );
        }

        if (rounds == null || rounds.isEmpty()) {
            throw new BusinessException(
                    "Nenhuma rodada foi fornecida."
            );
        }

        Set<Long> teamIds = new HashSet<>();

        for (TournamentsTeamsEntity team : teams) {
            if (team == null || team.getId() == null) {
                throw new BusinessException(
                        "A lista de equipes contém uma equipe inválida."
                );
            }

            if (!teamIds.add(team.getId())) {
                throw new BusinessException(
                        "A lista de equipes contém IDs duplicados."
                );
            }
        }

        int teamCount = teamIds.size();

        int expectedRounds = teamCount % 2 == 0
                ? teamCount - 1
                : teamCount;

        int expectedMatchesPerRound = teamCount / 2;

        if (rounds.size() != expectedRounds) {
            throw new BusinessException(
                    "Quantidade inválida de rodadas. Esperado: "
                            + expectedRounds
                            + ", fornecido: "
                            + rounds.size()
            );
        }

        Set<Integer> roundNumbers = new HashSet<>();
        Set<ManualMatchupRequest> allMatchups = new HashSet<>();

        for (ManualRoundRequest round : rounds) {

            if (round == null) {
                throw new BusinessException(
                        "Uma das rodadas informadas é inválida."
                );
            }

            int roundNumber = round.roundNumber();

            if (roundNumber < 1 || roundNumber > expectedRounds
                    || !roundNumbers.add(roundNumber)) {
                throw new BusinessException(
                        "Número de rodada inválido ou duplicado: "
                                + roundNumber
                );
            }

            List<ManualMatchupRequest> matchups = round.matchups();

            if (matchups == null
                    || matchups.size() != expectedMatchesPerRound) {
                throw new BusinessException(
                        "Quantidade inválida de partidas na rodada "
                                + roundNumber
                                + ". Esperado: "
                                + expectedMatchesPerRound
                );
            }

            Set<Long> teamsInRound = new HashSet<>();

            for (ManualMatchupRequest matchup : matchups) {

                if (matchup == null) {
                    throw new BusinessException(
                            "Confronto inválido na rodada "
                                    + roundNumber + "."
                    );
                }

                Long teamAId = matchup.teamAId();
                Long teamBId = matchup.teamBId();

                if (teamAId == null || teamBId == null) {
                    throw new BusinessException(
                            "As duas equipes são obrigatórias."
                    );
                }

                if (teamAId.equals(teamBId)) {
                    throw new BusinessException(
                            "Uma equipe não pode enfrentar a si mesma."
                    );
                }

                if (!teamIds.contains(teamAId)
                        || !teamIds.contains(teamBId)) {
                    throw new BusinessException(
                            "Uma das equipes não pertence ao torneio."
                    );
                }

                if (!teamsInRound.add(teamAId)
                        || !teamsInRound.add(teamBId)) {
                    throw new BusinessException(
                            "Uma equipe não pode participar de mais de "
                                    + "um confronto na rodada "
                                    + roundNumber + "."
                    );
                }

                if (!allMatchups.add(matchup)) {
                    throw new BusinessException(
                            "Confronto duplicado encontrado: "
                                    + teamAId + " vs " + teamBId
                    );
                }
            }
        }

        for (int roundNumber = 1;
             roundNumber <= expectedRounds;
             roundNumber++) {

            if (!roundNumbers.contains(roundNumber)) {
                throw new BusinessException(
                        "A rodada " + roundNumber + " não foi informada."
                );
            }
        }

        int expectedTotalMatchups =
                teamCount * (teamCount - 1) / 2;

        if (allMatchups.size() != expectedTotalMatchups) {
            throw new BusinessException(
                    "Quantidade total de confrontos inválida. Esperado: "
                            + expectedTotalMatchups
                            + ", fornecido: "
                            + allMatchups.size()
            );
        }

        Set<ManualMatchupRequest> expectedMatchups = new HashSet<>();
        List<Long> ids = new ArrayList<>(teamIds);

        for (int i = 0; i < ids.size(); i++) {
            for (int j = i + 1; j < ids.size(); j++) {
                expectedMatchups.add(
                        new ManualMatchupRequest(ids.get(i), ids.get(j))
                );
            }
        }

        if (!allMatchups.equals(expectedMatchups)) {
            throw new BusinessException(
                    "Nem todas as combinações esperadas entre "
                            + "as equipes foram informadas."
            );
        }

        return new ArrayList<>(rounds);
    }
}
