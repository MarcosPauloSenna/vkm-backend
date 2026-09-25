package com.vkm_backend.group.domain;

import com.vkm_backend.group.infra.persistence.GroupsEntity;
import com.vkm_backend.user.infra.persistence.UserEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GroupMembers {

    private Long id;
    private GroupsEntity groupId;
    private UserEntity useId;
    private GroupMemberRole role;
    private GroupMemberStatus status;
    private LocalDateTime approvedAt;
    private UserEntity approvedBy;

    public GroupMembers() {
        this.role = GroupMemberRole.MEMBER;
        this.status = GroupMemberStatus.PENDING;
    }
}
