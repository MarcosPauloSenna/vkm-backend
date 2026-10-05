package com.vkm_backend.teams.infra.persistence.web.controller;

import com.vkm_backend.group.service.GroupMemberAccessPolicy;
import com.vkm_backend.teams.infra.persistence.web.dto.FindOrCreateTeamRequest;
import com.vkm_backend.teams.infra.persistence.web.dto.FindOrCreateTeamResponse;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamInfoResponse;
import com.vkm_backend.teams.usecase.FindOrCreateTeamUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/teams")
@Tag(name = "Teams", description = "Cadastro, consulta e manutenção de equipes")
public class TeamsController {

    private final FindOrCreateTeamUseCase findOrCreateTeamUseCase;
    private final GroupMemberAccessPolicy groupMemberAccessPolicy;

    public TeamsController(FindOrCreateTeamUseCase findOrCreateTeamUseCase, GroupMemberAccessPolicy groupMemberAccessPolicy) {
        this.findOrCreateTeamUseCase = findOrCreateTeamUseCase;
        this.groupMemberAccessPolicy = groupMemberAccessPolicy;
    }

    @PostMapping("/create")
    public ResponseEntity<TeamInfoResponse> createTeam(@PathVariable Long groupId,
                                                       @RequestBody FindOrCreateTeamRequest request,
                                                       Authentication auth) {
        groupMemberAccessPolicy.authorize(groupId, auth.getName());
        FindOrCreateTeamResponse response = findOrCreateTeamUseCase.execute(request, groupId);

        return ResponseEntity.ok().body(new TeamInfoResponse(response, "Operação realizada com sucesso!"));
    }
}
