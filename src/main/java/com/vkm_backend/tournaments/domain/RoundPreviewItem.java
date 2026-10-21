package com.vkm_backend.tournaments.domain;

import java.util.List;

public record RoundPreviewItem(
        int roundNumber,
        List<MatchupPreviewItem> matchups
) {
}