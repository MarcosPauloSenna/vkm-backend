package com.vkm_backend.group.infra.persistence.repository;

import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.infra.global.specification.DynamicRepository;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface GroupMembersRepository extends DynamicRepository<GroupMembersEntity, Long> {

    boolean existsByGroupId_IdAndUserId_Id(Long groupId, Long userId);

    Optional<GroupMembersEntity> findByGroupId_IdAndUserId_Id(Long groupId, Long userId);

    GroupMembersEntity findByGroupIdAndUserId(GroupsEntity groupsEntity, UserEntity user);

    Optional<GroupMembersEntity> findByIdAndGroupId_Id(Long memberId, Long groupId);

    List<GroupMembersEntity> findByUserId(UserEntity user);
}
