package com.vkm_backend.teams.infra.persistence.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;

@Schema(description = "Dados necessários para criar ou localizar um time")
public record FindOrCreateTeamRequest(
        @Schema(description = "Nome do time. Se omitido, é gerado com as iniciais dos membros", example = "Time Azul", nullable = true)
        String name,
        @Schema(description = "Identificadores dos membros do grupo (group_members) que compõem o time", example = "[1, 2, 3]")
        Collection<Long> memberIds) {
}