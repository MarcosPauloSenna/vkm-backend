package com.vkm_backend.teams.service;

import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.teams.domain.CompositionHash;
import com.vkm_backend.teams.domain.TeamMembers;
import com.vkm_backend.teams.domain.Teams;
import com.vkm_backend.teams.infra.mapper.TeamMembersMapper;
import com.vkm_backend.teams.infra.mapper.TeamsMapper;
import com.vkm_backend.teams.infra.persistence.entities.TeamMembersEntity;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.teams.infra.persistence.repository.TeamMembersRepository;
import com.vkm_backend.teams.infra.persistence.repository.TeamsRepository;
import com.vkm_backend.teams.infra.persistence.web.dto.FindOrCreateTeamResponse;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamMembersResponse;
import com.vkm_backend.teams.infra.persistence.web.dto.ValidationResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CreateTeamService {

    private final TeamsRepository teamsRepository;
    private final TeamMembersRepository teamMembersRepository;
    private final TeamsMapper teamsMapper;
    private final TeamMembersMapper teamMembersMapper;

    public CreateTeamService(TeamsRepository teamsRepository, TeamMembersRepository teamMembersRepository, TeamsMapper teamsMapper, TeamMembersMapper teamMembersMapper) {
        this.teamsRepository = teamsRepository;
        this.teamMembersRepository = teamMembersRepository;
        this.teamsMapper = teamsMapper;
        this.teamMembersMapper = teamMembersMapper;
    }

    @Transactional
    public FindOrCreateTeamResponse execute(ValidationResponse request, String name, CompositionHash compositionHash) {

        TeamsEntity team = new TeamsEntity();
        team.setGroupId(request.group());
        team.setName(name);
        team.setTeamSize(request.teamMembers().size());
        team.setCompositionHash(compositionHash);
        TeamsEntity teamSave = teamsRepository.save(team);

        Teams teamResponse = teamsMapper.toDomain(teamSave);

        Collection<TeamMembersEntity> members = new ArrayList<>();

        for (GroupMembersEntity member : request.teamMembers()) {
            TeamMembersEntity teamMember = new TeamMembersEntity();
            teamMember.setTeamsId(teamSave);
            teamMember.setGroupMembersId(member);
            teamMembersRepository.save(teamMember);
            members.add(teamMember);
        }

        Collection<TeamMembers> teamMembers = new ArrayList<>();
        for (TeamMembersEntity member : members) {
            teamMembers.add(teamMembersMapper.toDomain(member));
        }

        TeamMembersResponse teamMembersResponse = teamMembersMapper.toResponse(teamMembers);


        return new FindOrCreateTeamResponse(teamResponse.getId(),
                teamResponse.getName(),
                teamResponse.getTeamSize(),
                teamMembersResponse);

    }
}
