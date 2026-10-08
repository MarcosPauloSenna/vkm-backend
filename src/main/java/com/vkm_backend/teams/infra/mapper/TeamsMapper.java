package com.vkm_backend.teams.infra.mapper;

import com.vkm_backend.infra.global.mapper.EntityMapper;
import com.vkm_backend.teams.domain.Teams;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamResponse;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamsGroupResponse;
import org.springframework.stereotype.Component;

@Component
public class TeamsMapper implements EntityMapper<TeamsEntity, Teams, TeamResponse> {
    @Override
    public TeamsEntity toEntity(Teams domain) {
        TeamsEntity entity = new TeamsEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    @Override
    public Teams toDomain(TeamsEntity entity) {
        Teams domain = new Teams();
        domain.setId(entity.getId());
        domain.setGroupId(entity.getGroupId());
        domain.setName(entity.getName());
        domain.setTeamSize(entity.getTeamSize());
        domain.setCompositionHash(entity.getCompositionHash());
        domain.setCreatedAt(entity.getCreatedAt());
        return domain;
    }

    @Override
    public TeamResponse toResponse(Teams domain) {
        return new TeamResponse(
                domain.getId(),
                domain.getGroupId().getName(),
                domain.getName(),
                domain.getTeamSize(),
                domain.getCreatedAt()
        );
    }

    public TeamsGroupResponse toResponse(TeamResponse resp) {
        return new TeamsGroupResponse(
                resp.id(),
                resp.groupName(),
                resp.teamSize(),
                resp.createdAt()
        );
    }
}
