package com.vkm_backend.teams.service;

import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.teams.domain.CompositionHash;
import com.vkm_backend.teams.domain.TeamMembers;
import com.vkm_backend.teams.infra.mapper.TeamMembersMapper;
import com.vkm_backend.teams.infra.mapper.TeamsMapper;
import com.vkm_backend.teams.infra.persistence.entities.TeamMembersEntity;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.teams.infra.persistence.repository.TeamMembersRepository;
import com.vkm_backend.teams.infra.persistence.repository.TeamsRepository;
import com.vkm_backend.teams.infra.persistence.web.dto.FindOrCreateTeamResponse;
import com.vkm_backend.teams.infra.persistence.web.dto.ValidationResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

        if (name == null || name.isEmpty()) {
            //Caso nome do time em branco, criar nome do time com as iniciais dos membros
            StringBuilder initials = new StringBuilder();
            for (GroupMembersEntity member : request.teamMembers()) {
                String[] nameParts = member.getUserId().getName().split(" ");
                for (String part : nameParts) {
                    if (!part.isEmpty()) {
                        initials.append(part.charAt(0));
                    }
                }
            }
            name = initials.toString();
        }

        TeamsEntity team = new TeamsEntity();
        team.setGroupId(request.group());
        team.setName(name);
        team.setTeamSize(request.teamMembers().size());
        team.setCompositionHash(compositionHash);
        team.setCreatedAt(Instant.now());
        TeamsEntity teamSave = teamsRepository.save(team);

        Collection<TeamMembersEntity> members = new ArrayList<>();

        for (GroupMembersEntity member : request.teamMembers()) {
            TeamMembersEntity teamMember = new TeamMembersEntity();
            teamMember.setTeamsId(teamSave);
            teamMember.setGroupMembersId(member);
            teamMember.setCreatedAt(Instant.now());
            teamMembersRepository.save(teamMember);
            members.add(teamMember);
        }

        Collection<TeamMembers> teamMembers = new ArrayList<>();
        for (TeamMembersEntity member : members) {
            teamMembers.add(teamMembersMapper.toDomain(member));
        }


        return teamMembersMapper.toResponse(teamMembers);

    }
}
