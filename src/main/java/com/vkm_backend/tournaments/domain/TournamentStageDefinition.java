package com.vkm_backend.tournaments.domain;

public record TournamentStageDefinition(    String name,
                                            TournamentsStageTypes stageType,
                                            Integer stageOrder) {
}
