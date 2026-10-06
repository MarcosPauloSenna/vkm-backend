package com.vkm_backend.teams.usecase;


import com.vkm_backend.teams.domain.CompositionHash;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.teams.infra.persistence.repository.TeamsRepository;
import com.vkm_backend.teams.infra.persistence.web.dto.FindOrCreateTeamRequest;
import com.vkm_backend.teams.infra.persistence.web.dto.FindOrCreateTeamResponse;
import com.vkm_backend.teams.infra.persistence.web.dto.ValidationResponse;
import com.vkm_backend.teams.service.CompositionHashGenerator;
import com.vkm_backend.teams.service.CreateTeamService;
import com.vkm_backend.teams.service.FindTeamMembersService;
import com.vkm_backend.teams.service.ValidateFormationTeamService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
public class FindOrCreateTeamUseCase {

    private final CompositionHashGenerator compositionHashGenerator;
    private final TeamsRepository teamsRepository;
    private final FindTeamMembersService findTeamMembersService;
    private final ValidateFormationTeamService validateFormationTeamService;
    private final CreateTeamService createTeamService;

    public FindOrCreateTeamUseCase(CompositionHashGenerator compositionHashGenerator, TeamsRepository teamsRepository, FindTeamMembersService findTeamMembersService, ValidateFormationTeamService validateFormationTeamService, CreateTeamService createTeamService) {
        this.compositionHashGenerator = compositionHashGenerator;
        this.teamsRepository = teamsRepository;
        this.findTeamMembersService = findTeamMembersService;
        this.validateFormationTeamService = validateFormationTeamService;
        this.createTeamService = createTeamService;
    }

    @Transactional
    public FindOrCreateTeamResponse execute(FindOrCreateTeamRequest request, Long groupId) {

        ValidationResponse teamValidationResponse = validateFormationTeamService.validateMemberInGroupId(request.memberIds(),
                groupId);

        CompositionHash compositionHash = compositionHashGenerator.generate(request.memberIds());

        TeamsEntity team = teamsRepository.findByCompositionHash(compositionHash);
        if (team != null) {
            return findTeamMembersService.execute(team);
        }

        return createTeamService.execute(teamValidationResponse, request.name(), compositionHash);


    }
}
