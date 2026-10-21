package com.vkm_backend.tournaments.infra.web.dto;

import com.vkm_backend.infra.global.exceptions.BusinessException;

public record ManualMatchupRequest(Long teamAId,
                                   Long teamBId) {
    public ManualMatchupRequest {
        if (teamAId == null || teamBId == null) {
            throw new BusinessException("As duas equipes são obrigatórias.");
        }

        long first = Math.min(teamAId, teamBId);
        long second = Math.max(teamAId, teamBId);

        teamAId = first;
        teamBId = second;
    }
}
