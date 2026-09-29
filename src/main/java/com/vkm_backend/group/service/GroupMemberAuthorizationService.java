package com.vkm_backend.group.service;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMembers;
import com.vkm_backend.group.infra.mapper.GroupMemberMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.infra.global.exceptions.GroupNotFoundException;
import com.vkm_backend.user.infra.persistence.UserEntity;
import com.vkm_backend.user.infra.persistence.UserRepository;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

public class GroupMemberAuthorizationService {

    private final GroupMembersRepository repository;
    private final GroupMemberMapper mapper;
    private final GroupsRepository groupsRepository;

    private final UserRepository userRepository;

    public GroupMemberAuthorizationService(GroupMembersRepository repository, GroupMemberMapper mapper, GroupsRepository groupsRepository, UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.groupsRepository = groupsRepository;
        this.userRepository = userRepository;
    }

    public GroupMembers authorize(Long groupId, String username) {
        GroupMembersEntity membersEntity = validUserAndGroup(groupId, username);

        return mapper.toDomain(membersEntity);
    }


    public GroupMembers authorizeAdminAndOwner(Long groupId, String username) {
        GroupMembersEntity membersEntity = validUserAndGroup(groupId, username);

        if (membersEntity.getRole() != GroupMemberRole.ADMIN &&
                membersEntity.getRole() != GroupMemberRole.OWNER) {
            throw new AccessDeniedException(
                    "Usuário não possui permissão para esta operação."
            );
        }

        return mapper.toDomain(membersEntity);
    }

    public GroupMembers authorizeOwner(Long groupId, String username) {
        GroupMembersEntity membersEntity = validUserAndGroup(groupId, username);

        if (membersEntity.getRole() != GroupMemberRole.OWNER) {
            throw new AccessDeniedException(
                    "Usuário não possui permissão para esta operação."
            );
        }

        return mapper.toDomain(membersEntity);
    }

    private GroupMembersEntity validUserAndGroup(Long groupId, String username) {
        UserEntity user = userRepository.findByUsername(username);
        Optional<GroupsEntity> group = groupsRepository.findById(groupId);

        if (group.isEmpty()){
            throw new GroupNotFoundException();
        }
        GroupMembersEntity membersEntity = repository.findByGroupIdAndUserId(group.get(), user);

        if (membersEntity == null) {
            throw new AccessDeniedException(
                    "Usuário não é membro deste grupo."
            );

        }

        return membersEntity;
    }

}
