package com.vkm_backend.teams.service;

import com.vkm_backend.teams.domain.TeamMembers;
import com.vkm_backend.teams.infra.mapper.TeamMembersMapper;
import com.vkm_backend.teams.infra.persistence.entities.TeamMembersEntity;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.teams.infra.persistence.repository.TeamMembersRepository;
import com.vkm_backend.teams.infra.persistence.web.dto.FindOrCreateTeamResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;


@Service
public class FindTeamMembersService {
    private final TeamMembersMapper teamMembersMapper;
    private final TeamMembersRepository teamMembersRepository;

    public FindTeamMembersService(TeamMembersMapper teamMembersMapper, TeamMembersRepository teamMembersRepository) {
        this.teamMembersMapper = teamMembersMapper;
        this.teamMembersRepository = teamMembersRepository;
    }

    @NonNull
    public FindOrCreateTeamResponse execute(TeamsEntity team) {
        Collection<TeamMembersEntity> teamMembers = teamMembersRepository.findByTeamsId(team);

        Collection<TeamMembers> Members = new ArrayList<>();
        for (TeamMembersEntity member : teamMembers) {
            Members.add(teamMembersMapper.toDomain(member));
        }

        return teamMembersMapper.toResponse(Members);
    }
}

