package com.vkm_backend.group.infra.persistence.web.controller;

import com.vkm_backend.group.infra.persistence.web.dto.*;
import com.vkm_backend.group.usecase.ActiveInativeGroupUseCase;
import com.vkm_backend.group.usecase.CreateGroupUseCase;
import com.vkm_backend.group.usecase.GroupFindUsecase;
import com.vkm_backend.group.usecase.UpdateGroupUseCase;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.handler.ErrorResponse;
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
@RequestMapping("/api/v1/groups")
@Tag(name = "Grupos", description = "Cadastro, consulta e manutenção de grupos")
public class GroupController {

    private final CreateGroupUseCase createGroupUseCase;

    private final GroupFindUsecase groupFindUsecase;

    private final UpdateGroupUseCase updateGroupUseCase;

    private final ActiveInativeGroupUseCase activeInative;

    public GroupController(CreateGroupUseCase createGroupUseCase, GroupFindUsecase groupFindUsecase, UpdateGroupUseCase updateGroupUseCase, ActiveInativeGroupUseCase activeInative) {
        this.createGroupUseCase = createGroupUseCase;
        this.groupFindUsecase = groupFindUsecase;
        this.updateGroupUseCase = updateGroupUseCase;
        this.activeInative = activeInative;
    }


    @PostMapping("/create")
    @Operation(
            summary = "Cria um novo grupo",
            description = "Cria um novo grupo de vôlei para o usuário autenticado. " +
                    "O grupo é criado com os dados informados na requisição e o usuário autenticado " +
                    "é definido como proprietário do grupo.",
            tags = {"Grupos"},
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Grupo criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateGroupResponse.class))),
            @ApiResponse(responseCode = "409", description = "Nome de grupo ja existe nesta cidade, digite outro nome.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Falha na operação. grupo com Id{GropId} não localizado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CreateGroupResponse> create(@Valid @RequestBody CreateGroupRequest request, Authentication auth) {
        String username = auth.getName();

        GroupResponse response = createGroupUseCase.createGroup(request, username);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateGroupResponse(response, "Grupo criado com successo."));
    }


    @GetMapping("/search")
    @Operation(
            summary = "Buscar grupos",
            description = "Busca grupos filtrando por id, name, description, city e state.",
            tags = {"Grupos"},
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso!",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateGroupResponse.class)))
    })
    public ResponseEntity<GroupsSearchResponse> search(@ModelAttribute GroupSearchRequest request, Pageable pageable) {
        PageResponse<GroupResponse> response = groupFindUsecase.search(request, pageable);

        return ResponseEntity.ok().body(new GroupsSearchResponse(response,
                "Operação realizada com sucesso!"));
    }


    @PatchMapping("/{groupId}")
    public ResponseEntity<GroupUpdateResponse> updateGroup(@PathVariable Long groupId,
                                                           @Valid @RequestBody UpdateGroupRequest request,
                                                           Authentication auth) {
        GroupResponse response = updateGroupUseCase.updateGroup(request, groupId, auth.getName());

        return ResponseEntity.ok().body(new GroupUpdateResponse(response,
                "Operação realizada com sucesso!"));
    }

    @PatchMapping("/{groupId}/status")
    public ResponseEntity<GroupUpdateResponse> activeInactive(@PathVariable Long groupId,
                                         @Valid @RequestBody UpdateGroupStatusRequest request,
                                         Authentication auth){
        GroupResponse response = activeInative.updadteStatus(request, groupId, auth.getName());
        return ResponseEntity.ok().body(new GroupUpdateResponse(response,
                "Operação realizada com sucesso!"));

    }
}
