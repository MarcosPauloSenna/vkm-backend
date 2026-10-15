package com.vkm_backend.tournaments.service;

import com.vkm_backend.tournaments.domain.TournamentStageDefinition;
import com.vkm_backend.tournaments.domain.TournamentsFormat;
import com.vkm_backend.tournaments.domain.TournamentsStageTypes;
import org.springframework.stereotype.Component;

@Component
public class TournamentStageFactory {

    public TournamentStageDefinition createInitialStage(
            TournamentsFormat format,
            int teamCount) {

        if (teamCount < 2) {
            throw new IllegalArgumentException(
                    "O torneio precisa ter pelo menos 2 equipes."
            );
        }

        return switch (format) {
            case ROUND_ROBIN ->
                    new TournamentStageDefinition(
                            "Todos contra todos",
                            TournamentsStageTypes.ROUND_ROBIN,
                            1
                    );

            case GROUPS, GROUPS_AND_KNOCKOUT ->
                    new TournamentStageDefinition(
                            "Fase de grupos",
                            TournamentsStageTypes.GROUP,
                            1
                    );

            case KNOCKOUT ->
                    createKnockoutInitialStage(teamCount);
        };
    }

    private TournamentStageDefinition createKnockoutInitialStage(
            int teamCount) {

        if (teamCount <= 2) {
            return definition(
                    "Final",
                    TournamentsStageTypes.FINAL
            );
        }

        if (teamCount <= 4) {
            return definition(
                    "Semifinal",
                    TournamentsStageTypes.SEMI_FINAL
            );
        }

        if (teamCount <= 8) {
            return definition(
                    "Quartas de final",
                    TournamentsStageTypes.QUARTER_FINAL
            );
        }

        if (teamCount <= 16) {
            return definition(
                    "Oitavas de final",
                    TournamentsStageTypes.ROUND_OF_16
            );
        }

        throw new IllegalArgumentException(
                "Quantidade de equipes acima do limite suportado."
        );
    }

    private TournamentStageDefinition definition(
            String name,
            TournamentsStageTypes type) {

        return new TournamentStageDefinition(name, type, 1);
    }
}