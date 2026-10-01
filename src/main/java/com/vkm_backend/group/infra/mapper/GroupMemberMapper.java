package com.vkm_backend.group.infra.mapper;

import com.vkm_backend.group.domain.GroupMembers;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.web.dto.MemberStatusResponse;
import com.vkm_backend.group.infra.persistence.web.dto.MembershipResponse;
import org.springframework.stereotype.Component;

@Component
public class GroupMemberMapper {

    public GroupMembers toDomain(GroupMembersEntity entity) {
        GroupMembers domain = new GroupMembers();
        domain.setId(entity.getId());
        domain.setGroupId(entity.getGroupId());
        domain.setRole(entity.getRole());
        domain.setStatus(entity.getStatus());
        domain.setUseId(entity.getUserId());
        domain.setApprovedAt(entity.getApprovedAt());

        return domain;
    }

    public MembershipResponse toResponse(GroupMembers domain) {
        return new MembershipResponse(domain.getId(),
                domain.getGroupId().getName(),
                domain.getUseId().getUsername(),
                domain.getRole().name(),
                domain.getStatus().name());
    }

    public MemberStatusResponse toStatusResponse(GroupMembers domain) {
        return new MemberStatusResponse(domain.getId(),
                domain.getUseId().getName(),
                domain.getRole().name(),
                domain.getStatus().name(),
                domain.getApprovedAt());

    }
}
