package com.vkm_backend.teams.usecase;

import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.teams.infra.persistence.repository.TeamsRepository;
import com.vkm_backend.teams.infra.persistence.web.dto.FindOrCreateTeamResponse;
import com.vkm_backend.teams.service.FindTeamMembersService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class FindCompositionTeamsUseCase {


    private final TeamsRepository teamsRepository;
    private final FindTeamMembersService findTeamMembersService;


    public FindCompositionTeamsUseCase(TeamsRepository teamsRepository, FindTeamMembersService findTeamMembersService) {
        this.findTeamMembersService = findTeamMembersService;
        this.teamsRepository = teamsRepository;

    }

    @Transactional
    public FindOrCreateTeamResponse execute(Long teamId) {

        TeamsEntity team = teamsRepository.findById(teamId).orElseThrow(() -> new BusinessException("Time não encontrado"));

        return findTeamMembersService.execute(team);

    }


}
