package com.vkm_backend.group.infra.persistence.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Schema(description = "Dados necessários para criar um novo grupo")
public record CreateGroupRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve possuir no máximo 100 caracteres")
        @Schema(description = "Nome do grupo", example = "Volei com Cristo", maxLength = 100)
        String name,


        @Size(max = 250, message = "Descrição deve possuir no máximo 250 caracteres")
        @Schema(description = "Descrição do grupo de volei",
                example = "Grupo de vôlei para quem ama esporte, amizade e diversão dentro e fora das quadras.",
                maxLength = 100)
        String description,

        @Size(max = 100, message = "Nome da cidade deve possuir no máximo 100 caracteres")
        @Schema(description = "Nome da cidade", example = "Cruz das almas", maxLength = 100)
        String city,

        @Size(max = 50, message = "Nome do estado deve possuir no máximo 50 caracteres")
        @Schema(description = "Nome do estado", example = "Bahia", maxLength = 50)
        String state) {
}
