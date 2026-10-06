package com.vkm_backend.teams.infra.persistence.web.dto;

import com.vkm_backend.group.infra.persistence.web.dto.ListTeamMembers;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;

@Schema(description = "Dados do time criado ou localizado")
public record FindOrCreateTeamResponse(
        @Schema(description = "Identificador do time", example = "10")
        Long id,
        @Schema(description = "Nome do time", example = "Time Azul")
        String name,
        @Schema(description = "Quantidade de membros do time", example = "6")
        Integer teamSize,
        @Schema(description = "Membros do grupo vinculados ao time")
        Collection<ListTeamMembers> members) {
}