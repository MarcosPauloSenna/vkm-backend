package com.vkm_backend.group.infra.persistence.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GroupSearchRequest(@Schema(description = "Id do grupo", example = "15")
                                 Long id,
                                 @Schema(description = "Nome do grupo", example = "Volei com Cristo", maxLength = 100)
                                 String name,

                                 @Schema(description = "Descrição do grupo de volei",
                                         example = "Grupo de vôlei para quem ama esporte, amizade e diversão dentro e fora das quadras.",
                                         maxLength = 250)
                                 String description,

                                 @Schema(description = "Nome da cidade", example = "Cruz das almas", maxLength = 100)
                                 String city,

                                 @Schema(description = "Nome do estado", example = "Bahia", maxLength = 50)
                                 String state) {
}
