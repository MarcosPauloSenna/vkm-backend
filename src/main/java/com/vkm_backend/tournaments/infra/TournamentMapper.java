package com.vkm_backend.tournaments.infra;


import com.vkm_backend.infra.global.mapper.EntityMapper;
import com.vkm_backend.tournaments.domain.Tournaments;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import com.vkm_backend.tournaments.infra.web.dto.TournamentResponse;



public class TournamentMapper implements EntityMapper<TournamentsEntity, Tournaments, TournamentResponse> {

    @Override
    public Tournaments toDomain(TournamentsEntity entity) {
        return null;
    }

    @Override
    public TournamentsEntity toEntity(Tournaments domain) {
        return null;
    }

    @Override
    public TournamentResponse toResponse(Tournaments domain) {
        return null;
    }


}

