package com.vkm_backend.group.infra.persistence.web.controller;

import com.vkm_backend.group.infra.persistence.web.dto.CreateGroupRequest;
import com.vkm_backend.group.infra.persistence.web.dto.CreateGroupResponse;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.group.usecase.CreateGroupUseCase;
import com.vkm_backend.infra.global.handler.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/groups")
@Tag(name = "Grupos", description = "Cadastro, consulta e manutenção de grupos")
public class GroupController {

    private final CreateGroupUseCase createGroupUseCase;

    public GroupController(CreateGroupUseCase createGroupUseCase) {
        this.createGroupUseCase = createGroupUseCase;
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
    public ResponseEntity<CreateGroupResponse> create(CreateGroupRequest request, Authentication auth){
        String username = auth.getName();

        GroupResponse response  = createGroupUseCase.createGroup(request, username);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateGroupResponse(response, "Grupo criado com successo."));
    }
}
