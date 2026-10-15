package com.vkm_backend.tournaments.infra.web.controller;

import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.handler.ErrorResponse;
import com.vkm_backend.tournaments.domain.TournamentsStatus;
import com.vkm_backend.tournaments.infra.web.dto.CreateTournamentRequest;
import com.vkm_backend.tournaments.infra.web.dto.RegisterTournamentsTeamsRequest;
import com.vkm_backend.tournaments.infra.web.dto.SearchTournamentRequest;
import com.vkm_backend.tournaments.infra.web.dto.SearchTournamentTeamsRequest;
import com.vkm_backend.tournaments.infra.web.dto.TournamentResponse;
import com.vkm_backend.tournaments.infra.web.dto.TournamentsTeamsResponse;
import com.vkm_backend.tournaments.infra.web.dto.UpdateTournamentStatusRequest;
import com.vkm_backend.tournaments.usecase.AproveTournamentTeamsUseCase;
import com.vkm_backend.tournaments.usecase.CreateTournamentsUseCase;
import com.vkm_backend.tournaments.usecase.RegisterTournamentsTeamsUseCase;
import com.vkm_backend.tournaments.usecase.SearchTournamentTeamsUseCase;
import com.vkm_backend.tournaments.usecase.SearchTournamentsUseCase;
import com.vkm_backend.tournaments.usecase.UpdateStatusTournamentsUseCase;
import com.vkm_backend.tournaments.usecase.UpdateTournamentsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/tournaments")
@Tag(name = "Torneios", description = "Criação, consulta e gerenciamento de torneios e inscrições de times")
public class TournamentsController {

    private final CreateTournamentsUseCase createTournamentsUseCase;
    private final SearchTournamentsUseCase searchTournamentsUseCase;
    private final UpdateTournamentsUseCase updateTournamentsUseCase;
    private final UpdateStatusTournamentsUseCase updateStatusTournamentsUseCase;
    private final RegisterTournamentsTeamsUseCase registerTournamentsTeamsUseCase;
    private final SearchTournamentTeamsUseCase searchTournamentTeamsUseCase;
    private final AproveTournamentTeamsUseCase aproveTournamentTeamsUseCase;

    public TournamentsController(CreateTournamentsUseCase createTournamentsUseCase,
                                 SearchTournamentsUseCase searchTournamentsUseCase,
                                 UpdateTournamentsUseCase updateTournamentsUseCase,
                                 UpdateStatusTournamentsUseCase updateStatusTournamentsUseCase,
                                 RegisterTournamentsTeamsUseCase registerTournamentsTeamsUseCase,
                                 SearchTournamentTeamsUseCase searchTournamentTeamsUseCase,
                                 AproveTournamentTeamsUseCase aproveTournamentTeamsUseCase) {
        this.createTournamentsUseCase = createTournamentsUseCase;
        this.searchTournamentsUseCase = searchTournamentsUseCase;
        this.updateTournamentsUseCase = updateTournamentsUseCase;
        this.updateStatusTournamentsUseCase = updateStatusTournamentsUseCase;
        this.registerTournamentsTeamsUseCase = registerTournamentsTeamsUseCase;
        this.searchTournamentTeamsUseCase = searchTournamentTeamsUseCase;
        this.aproveTournamentTeamsUseCase = aproveTournamentTeamsUseCase;
    }

    @PostMapping("/create")
    @Operation(
            summary = "Cria um torneio",
            description = "Cria um torneio no grupo informado. Requer que o usuário autenticado seja ADMIN ou OWNER do grupo.",
            tags = {"Torneios"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Torneio criado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para criar torneios",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Grupo não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TournamentResponse> create(@Parameter(description = "Identificador do grupo", example = "15")
                                                     @PathVariable Long groupId,
                                                     @Valid @RequestBody CreateTournamentRequest request,
                                                     Authentication authentication) {
        TournamentResponse response = createTournamentsUseCase.execute(authentication.getName(), request, groupId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/search")
    @Operation(
            summary = "Busca torneios do grupo",
            description = "Filtra os torneios do grupo e retorna os resultados paginados. Requer vínculo aprovado com o grupo.",
            tags = {"Torneios"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Torneios encontrados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuário não possui vínculo aprovado com o grupo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Grupo não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<TournamentResponse>> search(@PathVariable Long groupId,
                                                                   @Valid @RequestBody SearchTournamentRequest request,
                                                                   @ParameterObject Pageable pageable,
                                                                   Authentication authentication) {
        PageResponse<TournamentResponse> response = searchTournamentsUseCase.execute(
                groupId, request, pageable, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{tournamentId}")
    @Operation(
            summary = "Atualiza os dados de um torneio",
            description = "Atualiza os dados de um torneio em elaboração ou com inscrições abertas. Requer ADMIN ou OWNER do grupo.",
            tags = {"Torneios"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Torneio atualizado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Torneio não está em status permitido para atualização",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para atualizar torneios",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Grupo ou torneio não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TournamentResponse> update(@PathVariable Long groupId,
                                                     @PathVariable Long tournamentId,
                                                     @Valid @RequestBody CreateTournamentRequest request,
                                                     Authentication authentication) {
        TournamentResponse response = updateTournamentsUseCase.execute(
                request, groupId, tournamentId, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{tournamentId}/status")
    @Operation(
            summary = "Atualiza o status do torneio",
            description = "Altera o status conforme as transições permitidas e valida as condições para abrir inscrições ou iniciar o torneio. Requer ADMIN ou OWNER.",
            tags = {"Torneios"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TournamentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Status inválido ou transição não permitida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para alterar o status",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Grupo ou torneio não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TournamentResponse> updateStatus(@PathVariable Long groupId,
                                                           @PathVariable Long tournamentId,
                                                           @Valid @RequestBody UpdateTournamentStatusRequest request,
                                                           Authentication authentication) {
        TournamentResponse response = updateStatusTournamentsUseCase.execute(
                groupId, tournamentId, request.status(), authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{tournamentId}/teams")
    @Operation(
            summary = "Inscreve um time no torneio",
            description = "Inscreve um time do grupo em um torneio com inscrições abertas. O usuário precisa ser membro aprovado do grupo e todos os integrantes do time também devem estar aprovados.",
            tags = {"Torneios"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Time inscrito no torneio",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TournamentsTeamsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Torneio não recebe inscrições ou há integrantes não aprovados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuário não possui vínculo aprovado com o grupo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Grupo, torneio ou time não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TournamentsTeamsResponse> registerTeam(@PathVariable Long groupId,
                                                                 @PathVariable Long tournamentId,
                                                                 @Valid @RequestBody RegisterTournamentsTeamsRequest request,
                                                                 Authentication authentication) {
        TournamentsTeamsResponse response = registerTournamentsTeamsUseCase.register(
                request, tournamentId, authentication.getName(), groupId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{tournamentId}/teams/search")
    @Operation(
            summary = "Busca times inscritos no torneio",
            description = "Filtra as inscrições do torneio e retorna os resultados paginados. Requer vínculo aprovado com o grupo.",
            tags = {"Torneios"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscrições encontradas",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuário não possui vínculo aprovado com o grupo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Grupo ou torneio não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<TournamentsTeamsResponse>> searchTeams(@PathVariable Long groupId,
                                                                              @PathVariable Long tournamentId,
                                                                              @Valid @RequestBody SearchTournamentTeamsRequest request,
                                                                              @ParameterObject Pageable pageable,
                                                                              Authentication authentication) {
        PageResponse<TournamentsTeamsResponse> response = searchTournamentTeamsUseCase.execute(
                request, pageable, tournamentId, groupId, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{tournamentId}/teams/{teamId}/approval")
    @Operation(
            summary = "Aprova a inscrição de um time",
            description = "Aprova um time inscrito após validar os integrantes. Requer ADMIN ou OWNER do grupo.",
            tags = {"Torneios"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscrição aprovada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TournamentsTeamsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Inscrição inexistente ou não pode ser aprovada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para aprovar inscrições",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Grupo, torneio ou time não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TournamentsTeamsResponse> approveTeam(@PathVariable Long groupId,
                                                                @PathVariable Long tournamentId,
                                                                @PathVariable Long teamId,
                                                                Authentication authentication) {
        TournamentsTeamsResponse response = aproveTournamentTeamsUseCase.execute(
                teamId, tournamentId, groupId, authentication.getName());
        return ResponseEntity.ok(response);
    }
}
