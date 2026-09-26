package com.vkm_backend.group.infra.persistence.repository;

import com.vkm_backend.group.domain.GroupMembers;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMembersRepository extends JpaRepository<GroupMembersEntity, Long> {
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
}
