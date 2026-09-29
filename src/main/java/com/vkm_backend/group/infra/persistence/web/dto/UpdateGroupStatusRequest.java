package com.vkm_backend.group.infra.persistence.web.dto;

import com.vkm_backend.group.domain.GroupActive;
import jakarta.validation.constraints.NotNull;

public record UpdateGroupStatusRequest(@NotNull
                                       GroupActive status) {
}
