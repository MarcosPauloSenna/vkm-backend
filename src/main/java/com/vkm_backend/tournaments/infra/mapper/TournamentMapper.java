package com.vkm_backend.tournaments.infra.mapper;


import com.vkm_backend.infra.global.mapper.EntityMapper;
import com.vkm_backend.tournaments.domain.Tournaments;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import com.vkm_backend.tournaments.infra.web.dto.TournamentResponse;
import org.springframework.stereotype.Component;



@Component
public class TournamentMapper implements EntityMapper<TournamentsEntity, Tournaments, TournamentResponse> {

    @Override
    public Tournaments toDomain(TournamentsEntity entity) {
        Tournaments domain = new Tournaments();
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setDescription(entity.getDescription());
        domain.setStartDate(entity.getStartDate());
        domain.setEndDate(entity.getEndDate());
        domain.setStatus(entity.getStatus());
        domain.setFormat(entity.getFormat());
        domain.setGroupId(entity.getGroupId());
        domain.setCreatedAt(entity.getCreatedAt());
        return domain;
    }

    @Override
    public TournamentsEntity toEntity(Tournaments domain) {
        TournamentsEntity entity = new TournamentsEntity();
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setStatus(domain.getStatus());
        entity.setFormat(domain.getFormat());
        entity.setGroupId(domain.getGroupId());
        return entity;
    }

    @Override
    public TournamentResponse toResponse(Tournaments domain) {
        return new TournamentResponse(
                domain.getId(),
                domain.getName(),
                domain.getGroupId().getName(),
                domain.getDescription(),
                domain.getStartDate(),
                domain.getEndDate(),
                domain.getStatus(),
                domain.getFormat(),
                domain.getCreatedAt()
        );
    }


}
