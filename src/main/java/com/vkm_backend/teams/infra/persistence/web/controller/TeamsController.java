package com.vkm_backend.teams.infra.persistence.web.controller;

import com.vkm_backend.group.service.GroupMemberAccessPolicy;
import com.vkm_backend.teams.infra.persistence.web.dto.FindOrCreateTeamRequest;
import com.vkm_backend.teams.infra.persistence.web.dto.FindOrCreateTeamResponse;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamInfoResponse;
import com.vkm_backend.teams.usecase.FindOrCreateTeamUseCase;
import com.vkm_backend.infra.global.handler.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(
            summary = "Cria ou busca um time no grupo",
            description = "Cria um time com os membros informados dentro do grupo. Apenas membros APPROVED do grupo podem compor o time. " +
                    "Caso o nome seja omitido, ele é gerado com as iniciais dos membros. Caso já exista um time com a mesma composição de membros, " +
                    "o time existente é retornado. O usuário autenticado precisa ser membro aprovado do grupo.",
            tags = {"Teams"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Time criado ou localizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeamInfoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para formação do time",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou membro sem permissão (status diferente de APPROVED)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Grupo ou membro não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TeamInfoResponse> createTeam(@Parameter(description = "Identificador do grupo", example = "15")
                                                       @PathVariable Long groupId,
                                                       @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do time") @RequestBody FindOrCreateTeamRequest request,
                                                       Authentication auth) {
        groupMemberAccessPolicy.authorize(groupId, auth.getName());
        FindOrCreateTeamResponse response = findOrCreateTeamUseCase.execute(request, groupId);

        return ResponseEntity.ok().body(new TeamInfoResponse(response, "Operação realizada com sucesso!"));
    }
}
