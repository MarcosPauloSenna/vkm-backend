package com.vkm_backend.teams.infra.persistence.web.dto;

import com.vkm_backend.teams.domain.CompositionHash;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Resumo de um time")
public record TeamResponse(
        @Schema(description = "Identificador do time", example = "10")
        Long id,
        @Schema(description = "Nome do grupo", example = "Volei com Cristo")
        String groupName,
        @Schema(description = "Nome do time", example = "Time Azul")
        String teamName,
        @Schema(description = "Quantidade de membros", example = "6")
        Integer teamSize,
        @Schema(description = "Data de criação", example = "2026-10-05T17:26:41Z")
        Instant createdAt) {
}