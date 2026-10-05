package com.vkm_backend.teams.infra.mapper;


import com.vkm_backend.group.infra.persistence.web.dto.ListTeamMembers;
import com.vkm_backend.infra.global.mapper.EntityMapper;
import com.vkm_backend.teams.domain.TeamMembers;
import com.vkm_backend.teams.infra.persistence.entities.TeamMembersEntity;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamMembersResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class TeamMembersMapper implements EntityMapper<TeamMembersEntity, TeamMembers, TeamMembersResponse> {

    @Override
    public TeamMembersEntity toEntity(TeamMembers domain) {
        TeamMembersEntity entity = new TeamMembersEntity();
        entity.setId(domain.getId());
        entity.setTeamsId(domain.getTeamsId());
        entity.setGroupMembersId(domain.getGroupMembersId());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    @Override
    public TeamMembers toDomain(TeamMembersEntity entity) {
        TeamMembers teamMembers = new TeamMembers();
        teamMembers.setId(entity.getId());
        teamMembers.setTeamsId(entity.getTeamsId());
        teamMembers.setGroupMembersId(entity.getGroupMembersId());
        teamMembers.setCreatedAt(entity.getCreatedAt());
        return teamMembers;
    }

    @Override
    public TeamMembersResponse toResponse(TeamMembers domain) {
        return null;
    }


    public TeamMembersResponse toResponse(Collection<TeamMembers> domain) {
        Collection<ListTeamMembers> members = new ArrayList<>();

        for (TeamMembers member: domain) {
        members.add(new ListTeamMembers(member.getGroupMembersId().getId(),
                member.getGroupMembersId().getUserId().getName(),
                member.getGroupMembersId().getStatus().toString()));
        }

        return new TeamMembersResponse(
                domain.stream().findFirst().get().getId(),
                domain.stream().findFirst().get().getTeamsId().getName(),
                members,
                domain.stream().findFirst().get().getCreatedAt()
        );

    }


}
