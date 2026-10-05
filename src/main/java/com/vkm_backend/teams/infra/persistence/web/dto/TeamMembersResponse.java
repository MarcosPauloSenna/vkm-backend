package com.vkm_backend.teams.infra.persistence.web.dto;

import com.vkm_backend.group.infra.persistence.web.dto.ListTeamMembers;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Collection;

@Schema(description = "Membros que compõem o time")
public record TeamMembersResponse(
        @Schema(description = "Identificador do time", example = "10")
        Long id,
        @Schema(description = "Nome do time", example = "Time Azul")
        String teamsName,
        @Schema(description = "Membros do grupo vinculados ao time")
        Collection<ListTeamMembers> groupMembersId,
        @Schema(description = "Data de criação do vínculo", example = "2026-10-05T17:26:41Z")
        Instant createdAt) {
}