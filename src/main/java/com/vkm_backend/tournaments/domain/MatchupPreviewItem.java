package com.vkm_backend.tournaments.domain;

public record MatchupPreviewItem(int matchNumber,
                                 TeamPreviewItem teamA,
                                 TeamPreviewItem teamB) {
}
