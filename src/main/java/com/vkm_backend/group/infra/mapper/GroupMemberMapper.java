package com.vkm_backend.group.infra.mapper;

import com.vkm_backend.group.domain.GroupMembers;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
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
        domain.setUseId(entity.getUseId());

        return domain;
    }

    public MembershipResponse toResponse(GroupMembers domain) {
        return new MembershipResponse(domain.getId(),
                domain.getUseId().getUsername(),
                domain.getGroupId().getName(),
                domain.getRole().name(),
                domain.getStatus().name());
    }
}
