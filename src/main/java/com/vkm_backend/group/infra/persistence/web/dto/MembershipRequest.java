package com.vkm_backend.group.infra.persistence.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


public record MembershipRequest(@NotBlank(message = "Codigo do grupo não informado.")
                                @Schema(description = "Codigo do grupo de adesão", example = "15")
                                Long groupId,
                                @NotBlank(message = "Username do usuario não informado.")
                                @Schema(description = "Username do usuario", example = "Abacaxi12")
                                String username) {
}
