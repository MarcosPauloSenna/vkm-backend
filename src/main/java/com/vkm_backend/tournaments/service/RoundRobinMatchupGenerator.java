
package com.vkm_backend.tournaments.service;

import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.tournaments.domain.RoundPreviewItem;
import com.vkm_backend.tournaments.domain.MatchupPreviewItem;
import com.vkm_backend.tournaments.domain.TeamPreviewItem;
import com.vkm_backend.tournaments.infra.entities.TournamentsTeamsEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


@Component
public class RoundRobinMatchupGenerator {

    public List<RoundPreviewItem> generateRandom(
            List<TournamentsTeamsEntity> teams,
            Random random) {

        List<TournamentsTeamsEntity> shuffled = new ArrayList<>(teams);
        Collections.shuffle(shuffled, random);

        return generateRounds(shuffled);
    }

    public List<RoundPreviewItem> generateRounds(
            List<TournamentsTeamsEntity> teams) {

        if (teams == null || teams.size() < 2) {
            throw new BusinessException(
                    "São necessárias pelo menos duas equipes."
            );
        }

        List<TournamentsTeamsEntity> rotation = new ArrayList<>(teams);

        // Com quantidade ímpar, uma equipe folga por rodada.
        if (rotation.size() % 2 != 0) {
            rotation.add(null);
        }

        int totalRounds = rotation.size() - 1;
        int matchesPerRound = rotation.size() / 2;

        List<RoundPreviewItem> rounds = new ArrayList<>();

        for (int round = 1; round <= totalRounds; round++) {

            List<MatchupPreviewItem> matches = new ArrayList<>();

            for (int i = 0; i < matchesPerRound; i++) {

                TournamentsTeamsEntity teamA = rotation.get(i);
                TournamentsTeamsEntity teamB =
                        rotation.get(rotation.size() - 1 - i);

                // Uma equipe fictícia representa a folga.
                if (teamA == null || teamB == null) {
                    continue;
                }

                TeamPreviewItem previewA = new TeamPreviewItem(
                        teamA.getId(),
                        teamA.getTeamId().getName()
                );

                TeamPreviewItem previewB = new TeamPreviewItem(
                        teamB.getId(),
                        teamB.getTeamId().getName()
                );

                matches.add(new MatchupPreviewItem(
                        matches.size() + 1,
                        previewA,
                        previewB
                ));
            }

            rounds.add(new RoundPreviewItem(round, matches));

            // Mantém a primeira equipe fixa e rotaciona as demais.
            TournamentsTeamsEntity last =
                    rotation.remove(rotation.size() - 1);

            rotation.add(1, last);
        }

        return rounds;
    }
}
