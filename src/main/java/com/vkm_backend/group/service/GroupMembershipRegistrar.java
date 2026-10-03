package com.vkm_backend.group.service;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class GroupMembershipRegistrar implements MembershipRegistrar {

    private final GroupMembersRepository groupMembersRepository;

    public GroupMembershipRegistrar(GroupMembersRepository groupMembersRepository) {
        this.groupMembersRepository = groupMembersRepository;
    }

    @Override
    public GroupMembersEntity registerOwner(GroupsEntity group, UserEntity owner) {
        GroupMembersEntity member = new GroupMembersEntity();
        member.setGroupId(group);
        member.setUserId(owner);
        member.setRole(GroupMemberRole.OWNER);
        member.setStatus(GroupMemberStatus.APPROVED);
        member.setApprovedAt(Instant.now());
        return groupMembersRepository.save(member);
    }
}