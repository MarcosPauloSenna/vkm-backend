package com.vkm_backend.group.infra.persistence.web.dto;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;

import java.util.List;

public record GroupMemberFindRequest(Long id,
                                     String name,
                                     List<GroupMemberRole> role,
                                     List<GroupMemberStatus> status) {
}
