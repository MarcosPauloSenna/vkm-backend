package com.vkm_backend.teams.infra.persistence.web.dto;

import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import java.util.Collection;


public record ValidationResponse(GroupsEntity group, Collection<GroupMembersEntity> teamMembers) {
}
