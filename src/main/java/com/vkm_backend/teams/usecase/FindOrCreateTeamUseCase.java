package com.vkm_backend.teams.usecase;


import com.vkm_backend.group.infra.persistence.web.dto.ListTeamMembers;
import com.vkm_backend.teams.domain.CompositionHash;
import com.vkm_backend.teams.domain.TeamMembers;
import com.vkm_backend.teams.domain.Teams;
import com.vkm_backend.teams.infra.mapper.TeamMembersMapper;
import com.vkm_backend.teams.infra.mapper.TeamsMapper;
import com.vkm_backend.teams.infra.persistence.entities.TeamMembersEntity;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.teams.infra.persistence.repository.TeamMembersRepository;
import com.vkm_backend.teams.infra.persistence.repository.TeamsRepository;
import com.vkm_backend.teams.infra.persistence.web.dto.FindOrCreateTeamRequest;
import com.vkm_backend.teams.infra.persistence.web.dto.FindOrCreateTeamResponse;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamMembersResponse;
import com.vkm_backend.teams.infra.persistence.web.dto.ValidationResponse;
import com.vkm_backend.teams.service.CompositionHashGenerator;
import com.vkm_backend.teams.service.CreateTeamService;
import com.vkm_backend.teams.service.ValidateFormationTeamService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class FindOrCreateTeamUseCase {

    private final CompositionHashGenerator compositionHashGenerator;
    private final TeamsRepository teamsRepository;
    private final TeamMembersRepository teamMembersRepository;
    private final ValidateFormationTeamService validateFormationTeamService;
    private final CreateTeamService createTeamService;
    private final TeamsMapper teamsMapper;
    private final TeamMembersMapper teamMembersMapper;

    public FindOrCreateTeamUseCase(CompositionHashGenerator compositionHashGenerator, TeamsRepository teamsRepository, TeamMembersRepository teamMembersRepository, ValidateFormationTeamService validateFormationTeamService, CreateTeamService createTeamService, TeamsMapper teamsMapper, TeamMembersMapper teamMembersMapper, TeamsMapper teamsMapper1, TeamMembersMapper teamMembersMapper1) {
        this.compositionHashGenerator = compositionHashGenerator;
        this.teamsRepository = teamsRepository;
        this.teamMembersRepository = teamMembersRepository;
        this.validateFormationTeamService = validateFormationTeamService;
        this.createTeamService = createTeamService;
        this.teamsMapper = teamsMapper1;
        this.teamMembersMapper = teamMembersMapper1;
    }

    @Transactional
    public FindOrCreateTeamResponse execute(FindOrCreateTeamRequest request) {

        ValidationResponse teamValidationResponse = validateFormationTeamService.validateMemberInGroupId(request.memberIds(), request.groupId());

        CompositionHash compositionHash = compositionHashGenerator.generate(request.memberIds());

        TeamsEntity team = teamsRepository.findByCompositionHash(compositionHash);
        if (team != null) {
            Collection<TeamMembersEntity> teamMembers = teamMembersRepository.findByTeamsId(team);

            Collection<TeamMembers> Members = new ArrayList<>();
            for (TeamMembersEntity member : teamMembers) {
                Members.add(teamMembersMapper.toDomain(member));
            }

            TeamMembersResponse teamMembersResponse = teamMembersMapper.toResponse(Members);

            return new FindOrCreateTeamResponse(team.getId(), team.getName(), team.getTeamSize(), teamMembersResponse);
        }

        return createTeamService.execute(teamValidationResponse, request.name(), compositionHash);


    }
}
