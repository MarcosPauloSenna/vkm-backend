package com.vkm_backend.tournaments.infra.mapper;

import com.vkm_backend.infra.global.mapper.EntityMapper;
import com.vkm_backend.tournaments.domain.TournamentsTeams;
import com.vkm_backend.tournaments.infra.entities.TournamentsTeamsEntity;
import com.vkm_backend.tournaments.infra.web.dto.TournamentsTeamsResponse;
import org.springframework.stereotype.Component;

@Component
public class TournamentTeamsMapper implements EntityMapper<TournamentsTeamsEntity, TournamentsTeams, TournamentsTeamsResponse> {

    @Override
    public TournamentsTeams toDomain(TournamentsTeamsEntity entity) {
        TournamentsTeams domain = new TournamentsTeams();
        domain.setId(entity.getId());
        domain.setTournamentId(entity.getTournamentId());
        domain.setSeed(entity.getSeed());
        domain.setGroupName(entity.getGroupName());
        domain.setTeamId(entity.getTeamId());
        domain.setStatus(entity.getStatus());
        domain.setCreatedAt(entity.getCreatedAt());
        return domain;
    }

    @Override
    public TournamentsTeamsEntity toEntity(TournamentsTeams domain) {
        TournamentsTeamsEntity entity = new TournamentsTeamsEntity();
        entity.setId(domain.getId());
        entity.setTournamentId(domain.getTournamentId());
        entity.setSeed(domain.getSeed());
        entity.setGroupName(domain.getGroupName());
        entity.setStatus(domain.getStatus());
        return entity;
    }

    @Override
    public TournamentsTeamsResponse toResponse(TournamentsTeams domain) {
        return new TournamentsTeamsResponse(
                domain.getId(),
                domain.getTournamentId().getName(),
                domain.getTeamId().getName(),
                domain.getStatus(),
                domain.getCreatedAt()
        );
    }
}
