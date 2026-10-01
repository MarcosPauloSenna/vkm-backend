package com.vkm_backend.group.infra.persistence.web.dto;

import com.vkm_backend.group.domain.GroupMemberStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record MemberStatusRequest(
        @NotNull(message = "Status do membro não informado.")
        GroupMemberStatus status) {
}
