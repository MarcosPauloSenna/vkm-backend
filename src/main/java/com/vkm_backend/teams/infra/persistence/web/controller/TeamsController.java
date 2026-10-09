package com.vkm_backend.teams.infra.persistence.web.controller;

import com.vkm_backend.group.service.GroupMemberAccessPolicy;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.handler.ErrorResponse;
import com.vkm_backend.teams.infra.persistence.web.dto.*;
import com.vkm_backend.teams.usecase.FindCompositionTeamsUseCase;
import com.vkm_backend.teams.usecase.FindOrCreateTeamUseCase;
import com.vkm_backend.teams.usecase.SearchTeamsUseCase;
import com.vkm_backend.teams.usecase.UpadateNameTeamUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/teams")
@Tag(name = "Teams", description = "Cadastro, consulta e manutenção de equipes")
public class TeamsController {

    private final FindOrCreateTeamUseCase findOrCreateTeamUseCase;
    private final GroupMemberAccessPolicy groupMemberAccessPolicy;
    private final FindCompositionTeamsUseCase findCompositionTeamsUseCase;
    private final SearchTeamsUseCase searchTeamsUseCase;
    private final UpadateNameTeamUseCase upadateNameTeamUseCase;

    public TeamsController(FindOrCreateTeamUseCase findOrCreateTeamUseCase, GroupMemberAccessPolicy groupMemberAccessPolicy, FindCompositionTeamsUseCase findCompositionTeamsUseCase, SearchTeamsUseCase searchTeamsUseCase, UpadateNameTeamUseCase upadateNameTeamUseCase) {
        this.findOrCreateTeamUseCase = findOrCreateTeamUseCase;
        this.groupMemberAccessPolicy = groupMemberAccessPolicy;
        this.findCompositionTeamsUseCase = findCompositionTeamsUseCase;
        this.searchTeamsUseCase = searchTeamsUseCase;
        this.upadateNameTeamUseCase = upadateNameTeamUseCase;
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

    @GetMapping("/{teamId}")
    @Operation(
            summary = "Busca um time pelo ID",
            description = "Busca um time pelo seu identificador. O usuário autenticado precisa ser membro aprovado do grupo.",
            tags = {"Teams"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Time localizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeamInfoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para formação do time",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou membro sem permissão (status diferente de APPROVED)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Grupo ou membro não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não é membro deste grupo.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TeamInfoResponse> getTeam(@Parameter(description = "Identificador do time", example = "15")
                                                    @PathVariable Long teamId,
                                                    @PathVariable Long groupId,
                                                    Authentication auth) {
        groupMemberAccessPolicy.authorize(groupId, auth.getName());
        FindOrCreateTeamResponse response = findCompositionTeamsUseCase.execute(teamId, groupId);

        return ResponseEntity.ok().body(new TeamInfoResponse(response, "Operação realizada com sucesso!"));
    }

    @PostMapping("/search")
    @Operation(
            summary = "Busca times dentro de um grupo",
            description = "Busca times dentro de um grupo com base nos critérios fornecidos. O usuário autenticado precisa ser membro aprovado do grupo.",
            tags = {"Teams"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso!",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeamsGroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para a busca de times",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou membro sem permissão (status diferente de APPROVED)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Grupo não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity getTeams(@PathVariable Long groupId,
                                   @RequestBody TeamsSearchRequest request,
                                   Pageable pageable,
                                   Authentication auth) {

        groupMemberAccessPolicy.authorize(groupId, auth.getName());
        PageResponse<TeamsGroupResponse> response = searchTeamsUseCase.execute(groupId, request, pageable);


        return ResponseEntity.ok().body(response);
    }


    @PatchMapping("/{teamId}/name")
    @Operation(
            summary = "Atualiza o nome de um time",
            description = "Atualiza o nome de um time dentro de um grupo. O usuário autenticado precisa ser membro aprovado do grupo.",
            tags = {"Teams"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso!",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeamsGroupResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para a atualização do nome do time",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou membro sem permissão (status diferente de APPROVED)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Grupo ou time não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity updateTeamName(@PathVariable Long groupId,
                                            @PathVariable Long teamId,
                                            @Valid @RequestBody UpdateNameRequest request,
                                            Authentication auth) {
        groupMemberAccessPolicy.authorize(groupId, auth.getName());
        TeamsGroupResponse response = upadateNameTeamUseCase.execute(teamId, request.newName(), groupId, auth.getName());

        return ResponseEntity.ok().body(new UpdateNameResponse(response, "Operação realizada com sucesso!"));
    }
}
