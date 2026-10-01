package com.vkm_backend.group.infra.persistence.web.controller;

import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.infra.persistence.web.dto.*;
import com.vkm_backend.group.usecase.GroupMemberFindUseCase;
import com.vkm_backend.group.usecase.RequestMembershipUseCase;
import com.vkm_backend.group.usecase.UpadateStatusMemberUseCase;
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
@RequestMapping("/api/v1/group/{groupId}/members")
@Tag(name = "Membros", description = "Cadastro, consulta, aprovação e manutenção de membros")
public class GroupMemberController {

    private final RequestMembershipUseCase requestMembershipUseCase;
    private final GroupMemberFindUseCase groupMemberFindUseCase;
    private final UpadateStatusMemberUseCase updateStatus;

    public GroupMemberController(RequestMembershipUseCase requestMembershipUseCase, GroupMemberFindUseCase groupMemberFindUseCase, UpadateStatusMemberUseCase updateStatus) {
        this.requestMembershipUseCase = requestMembershipUseCase;
        this.groupMemberFindUseCase = groupMemberFindUseCase;
        this.updateStatus = updateStatus;
    }

    @PostMapping("/associate")
    @Operation(
            summary = "Associa usuário a um grupo",
            description = "Associa o usuário autenticado a um grupo de vôlei informado pelo identificador do grupo. " +
                    "O usuário autenticado é utilizado para realizar a associação e não é necessário informar seu identificador na requisição. " +
                    "Caso o usuário já esteja associado ao grupo, a operação não será realizada. Por padrão, entrada no grupo fica com o status " +
                    "PENDING até a aprovação do ADMIN/OWNER. Usuarios associados são criados com o role MEMBER por padrão.",
            tags = {"Membros"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuário associado ao grupo com sucesso"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Usuário já associado a este grupo.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Violação de integridade ao realizar a associação.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<MemberResponse> associate(@PathVariable("groupId") Long groupId, Authentication auth) {

        String username = auth.getName();

        MembershipResponse response = requestMembershipUseCase.associate(
                new MembershipRequest(groupId, username));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MemberResponse(response,
                        "Solicitação de entrada ao grupo '" + response.groupName() + "' enviado com sucesso."));

    }

    @GetMapping("/search")
    public ResponseEntity<GroupMembersSearchResponse> search(@ModelAttribute GroupMemberFindRequest request,
                                                             @PathVariable Long groupId,
                                                             Pageable pageable,
                                                             Authentication auth) {

        PageResponse<MemberFindResponse> response = groupMemberFindUseCase.searchMembers(request,
                groupId, auth.getName(), pageable);

        return ResponseEntity.ok()
                .body(new GroupMembersSearchResponse(response, "Operação realizada com sucesso!"));


    }

    @PatchMapping("/{memberId}/status")
    public ResponseEntity<MemberStatusResponse> status(@PathVariable Long groupId,
                                                       @PathVariable Long memberId,
                                                       @Valid @RequestBody MemberStatusRequest request,
                                                       Authentication auth) {

        MemberFindResponse response = updateStatus.updateStatusMember(groupId, memberId, auth.getName(), request);

        return ResponseEntity.ok().body(new MemberStatusResponse(response, "Operação realizada com sucesso!"));

    }
}
