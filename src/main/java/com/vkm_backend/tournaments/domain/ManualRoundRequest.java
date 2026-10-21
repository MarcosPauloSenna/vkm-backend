package com.vkm_backend.tournaments.domain;

import com.vkm_backend.tournaments.infra.web.dto.ManualMatchupRequest;

import java.util.List;

public record ManualRoundRequest(
        int roundNumber,
        List<ManualMatchupRequest> matchups
) {
}
