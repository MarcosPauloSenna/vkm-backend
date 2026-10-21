package com.vkm_backend.tournaments.infra.web.dto;

import com.vkm_backend.tournaments.domain.MatchupGenerationMode;
import com.vkm_backend.tournaments.domain.RoundPreviewItem;
import com.vkm_backend.tournaments.domain.TournamentsFormat;

import java.util.List;

public record MatchupPreviewResponse(Long tournamentId,
                                     Long stageId,
                                     TournamentsFormat format,
                                     MatchupGenerationMode mode,
                                     int totalMatchups,
                                     int totalRounds,
                                     List<RoundPreviewItem> rounds) {
}
