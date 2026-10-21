package com.vkm_backend.tournaments.domain;

import com.vkm_backend.infra.global.exceptions.BusinessException;

public record MatchupPair(Long teamAId,
                          Long teamBId) {
    public MatchupPair {
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
    }
}
