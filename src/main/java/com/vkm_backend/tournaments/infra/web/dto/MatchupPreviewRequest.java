package com.vkm_backend.tournaments.infra.web.dto;

import com.vkm_backend.tournaments.domain.ManualRoundRequest;
import com.vkm_backend.tournaments.domain.MatchupGenerationMode;

import java.util.List;

public record MatchupPreviewRequest(MatchupGenerationMode mode,
                                    List<ManualRoundRequest> rounds) {
}
