package com.vkm_backend.group.infra.persistence.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;

import java.time.LocalDateTime;

public record MemberFindResponse(Long id,
                                 String name,
                                 String role,
                                 String status,
                                 @JsonFormat(pattern = "dd/MM/yyyy")
                                 LocalDateTime startGroup) {
}
