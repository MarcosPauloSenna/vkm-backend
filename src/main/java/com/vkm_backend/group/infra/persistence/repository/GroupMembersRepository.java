package com.vkm_backend.group.infra.persistence.repository;

import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.infra.global.specification.DynamicRepository;
import com.vkm_backend.user.infra.persistence.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMembersRepository extends DynamicRepository<GroupMembersEntity, Long> {

    boolean existsByGroupId_IdAndUserId_Id(Long groupId, Long userId);

    GroupMembersEntity findByGroupIdAndUserId(GroupsEntity groupsEntity, UserEntity user);

}
