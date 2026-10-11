package com.vkm_backend.group.service;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.AccessPolicyResponse;
import com.vkm_backend.infra.global.exceptions.GroupNotFoundException;
import com.vkm_backend.infra.global.exceptions.MemberNotFoundException;
import com.vkm_backend.infra.global.exceptions.ValidationException;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import com.vkm_backend.user.infra.persistence.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GroupMemberAccessPolicy implements GroupAccessPolicy {

    private final GroupMembersRepository repository;
    private final GroupsRepository groupsRepository;

    private final UserRepository userRepository;

    public GroupMemberAccessPolicy(GroupMembersRepository repository, GroupsRepository groupsRepository, UserRepository userRepository) {
        this.repository = repository;
        this.groupsRepository = groupsRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AccessPolicyResponse authorize(Long groupId, String username) {

        return validUserAndGroup(groupId, username);
    }


    @Override
    public AccessPolicyResponse authorizeAdminAndOwner(Long groupId, String username) {
        AccessPolicyResponse response = validUserAndGroup(groupId, username);

        if (response.groupMembers().getRole() != GroupMemberRole.ADMIN &&
                response.groupMembers().getRole() != GroupMemberRole.OWNER) {
            throw new AccessDeniedException(
                    "Usuário não possui permissão para esta operação."
            );
        }

        return response;
    }

    @Override
    public AccessPolicyResponse authorizeOwner(Long groupId, String username) {
        AccessPolicyResponse response = validUserAndGroup(groupId, username);

        if (response.groupMembers().getRole() != GroupMemberRole.OWNER) {
            throw new AccessDeniedException(
                    "Usuário não possui permissão para esta operação."
            );
        }

        return response;
    }

    private AccessPolicyResponse validUserAndGroup(Long groupId, String username) {
        UserEntity user = userRepository.findByUsername(username);
        Optional<GroupsEntity> group = groupsRepository.findById(groupId);

        if (group.isEmpty()){
            throw new GroupNotFoundException();
        }
        GroupMembersEntity membersEntity = repository.findByGroupIdAndUserId(group.get(), user);

        if (membersEntity == null) {
            throw new MemberNotFoundException(
                    "Usuário não é membro deste grupo."
            );

        }

        if (!membersEntity.getStatus().equals(GroupMemberStatus.APPROVED)){
            throw new ValidationException(
                    "Usuário não possui permissão para esta operação. Status: " + membersEntity.getStatus()
            );
        }

        return new AccessPolicyResponse(membersEntity, group.get());
    }

}
