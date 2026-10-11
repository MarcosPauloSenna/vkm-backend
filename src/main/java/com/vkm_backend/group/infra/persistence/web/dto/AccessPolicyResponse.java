package com.vkm_backend.group.infra.persistence.web.dto;

import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;

public record AccessPolicyResponse(GroupMembersEntity groupMembers,
                                   GroupsEntity groups) {

}
