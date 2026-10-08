package com.vkm_backend.user.infra.web.controllers;

import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.group.infra.persistence.web.dto.GroupSearchRequest;
import com.vkm_backend.group.infra.persistence.web.dto.GroupsSearchResponse;
import com.vkm_backend.group.usecase.ListMyGroupsUseCase;
import com.vkm_backend.group.usecase.ListUserGroupsUseCase;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.handler.ErrorResponse;
import com.vkm_backend.teams.infra.persistence.web.dto.MyTeamsSearchRequest;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamResponse;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamsSearchRequest;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamsSearchResponse;
import com.vkm_backend.teams.usecase.SearchMyTeamsUseCase;
import com.vkm_backend.user.infra.web.dto.*;
import com.vkm_backend.user.usecase.FindUsersUseCase;
import com.vkm_backend.user.usecase.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "Usuários", description = "Cadastro, consulta e manutenção de usuários")
public class UserController {

    private final UserUseCase userUseCase;
    private final ListMyGroupsUseCase listMyGroupsUseCase;
    private final FindUsersUseCase findUsersUseCase;
    private final ListUserGroupsUseCase listUserGroupsUseCase;
    private final SearchMyTeamsUseCase searchMyTeamsUseCase;

    public UserController(UserUseCase userUseCase, ListMyGroupsUseCase listMyGroupsUseCase, FindUsersUseCase findUsersUseCase, ListUserGroupsUseCase listUserGroupsUseCase, SearchMyTeamsUseCase searchMyTeamsUseCase) {

        this.userUseCase = userUseCase;
        this.listMyGroupsUseCase = listMyGroupsUseCase;
        this.findUsersUseCase = findUsersUseCase;
        this.listUserGroupsUseCase = listUserGroupsUseCase;
        this.searchMyTeamsUseCase = searchMyTeamsUseCase;
    }

    @PostMapping("/create")
    @Operation(
            summary = "Criar usuário",
            description = "Cadastra um novo usuário, validando username e telefone únicos e armazenando a senha com hash BCrypt.",
            tags = {"Usuários"},
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados obrigatórios ausentes ou inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Username ou telefone já cadastrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CreateUserResponse> saveUser(@Valid @RequestBody CreateUserRequest request) {

        UserResponse response = userUseCase.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserResponse("Usuario cadastrado com Sucesso!", response));
    }

    @GetMapping("/searchall")
    @Operation(
            summary = "Listar todos os usuários",
            description = "Retorna os usuários cadastrados, podendo ser aplicados filtros. Operação restrita a usuários com papel administrativo.",
            tags = {"Usuários"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuários retornados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserAdmResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token de acesso ausente ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão administrativa",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserAdmResponse> findAllUsers(@ModelAttribute FindUserAdmRequest request, Pageable pageable) {

        PageResponse<UserResponse> response = findUsersUseCase.execute(request, pageable);

        return ResponseEntity.ok(new UserAdmResponse(response, "Operação realizada com sucesso!"));

    }

    @GetMapping("/{userId}/groups")
    @Operation(
            summary = "Listar grupos do usuário informado",
            description = "Retorna apenas os grupos dos quais o usuário informado participa (qualquer status de vínculo), " +
                    "nunca a lista completa de grupos do sistema. Suporta os mesmos filtros de busca de grupos (id, name, " +
                    "description, city, state) restritos ao escopo do usuário informado. Endpoint restrito a usuários com papel administrativo.",
            tags = {"Usuários"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grupos do usuário retornados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GroupsSearchResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token de acesso ausente ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuário não autorizado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<GroupsSearchResponse> searchUserGroups(@ModelAttribute GroupSearchRequest request,
                                                                 Pageable pageable,
                                                                 @PathVariable Long userId) {
        PageResponse<GroupResponse> response = listUserGroupsUseCase.execute(request, userId, pageable);

        return ResponseEntity.ok().body(new GroupsSearchResponse(response,
                "Operação realizada com sucesso!"));
    }


    @GetMapping("/me")
    @Operation(
            summary = "Consultar meu perfil",
            description = "Retorna os dados do usuário identificado pelo access token enviado na requisição.",
            tags = {"Usuários"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token de acesso ausente ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário autenticado não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {


        return ResponseEntity.ok(userUseCase.getMe(authentication.getName()));

    }

    @PatchMapping("/me")
    @Operation(
            summary = "Atualizar meu perfil",
            description = "Atualiza parcialmente os dados do perfil do usuário autenticado.",
            tags = {"Usuários"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Nenhum dado informado ou dados inválidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token de acesso ausente ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Telefone já cadastrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody UpdateUserRequest user, Authentication authentication) {

        return ResponseEntity.ok(userUseCase.updateMe(user, authentication.getName()));

    }


    @PatchMapping("/password")
    @Operation(
            summary = "Atualizar senha",
            description = "Substitui a senha do usuário autenticado e armazena o novo valor com hash BCrypt.",
            tags = {"Usuários"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Senha ausente ou inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token de acesso ausente ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> updatePassword(@Valid @RequestBody
                                                       UpdatePasswordRequest request,
                                                       Authentication authentication) {

        return ResponseEntity.ok(userUseCase.updatePassword(request.newPassword(), authentication.getName()));
    }

    @GetMapping("/mygroups")
    @Operation(
            summary = "Listar meus grupos",
            description = "Retorna apenas os grupos dos quais o usuário autenticado participa (qualquer status de vínculo), " +
                    "nunca a lista completa de grupos do sistema. Suporta os mesmos filtros de busca de grupos (id, name, " +
                    "description, city, state) restritos ao escopo do usuário autenticado.",
            tags = {"Usuários"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grupos do usuário retornados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GroupsSearchResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token de acesso ausente ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<GroupsSearchResponse> searchMyGroups(@ModelAttribute GroupSearchRequest request,
                                                               Pageable pageable, Authentication auth) {
        PageResponse<GroupResponse> response = listMyGroupsUseCase.execute(request, auth.getName(), pageable);

        return ResponseEntity.ok().body(new GroupsSearchResponse(response,
                "Operação realizada com sucesso!"));
    }

    @PostMapping ("/myteams")
    @Operation(
            summary = "Listar meus times",
            description = "Retorna apenas os times dos quais o usuário autenticado participa (qualquer status de vínculo), " +
                    "nunca a lista completa de times do sistema. Suporta os mesmos filtros de busca de times (id, name, " +
                    "memberIds, createdAtFrom, createdAtTo, size, memberFilterMode) restritos ao escopo do usuário autenticado.",
            tags = {"Usuários"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Times do usuário retornados com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeamsSearchResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token de acesso ausente ou inválido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TeamsSearchResponse> searchMyTeams(@RequestBody MyTeamsSearchRequest request,
                                          Pageable pageable, Authentication auth) {

        TeamsSearchRequest dada = new TeamsSearchRequest(request.id(),
                request.name(),
                request.memberIds(),
                request.createdAtFrom(),
                request.createdAtTo(),
                request.size(),
                request.memberFilterMode());

        PageResponse<TeamResponse> response = searchMyTeamsUseCase.execute(request.groupId(), auth.getName(), dada, pageable);

        return ResponseEntity.ok().body(new TeamsSearchResponse(response,
                "Operação realizada com sucesso!"));
    }


}
