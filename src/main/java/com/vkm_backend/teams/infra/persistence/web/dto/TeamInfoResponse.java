package com.vkm_backend.teams.infra.persistence.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta da operação de criação/busca de time")
public record TeamInfoResponse(
        @Schema(description = "Dados do time")
        FindOrCreateTeamResponse teamMembersResponse,
        @Schema(description = "Mensagem da operação", example = "Operação realizada com sucesso!")
        String message) {
}