package com.vkm_backend.group.service;

import com.vkm_backend.group.infra.mapper.GroupMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.group.infra.persistence.web.dto.GroupSearchRequest;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.mapper.PageResponseMapper;
import com.vkm_backend.infra.global.specification.DynamicFilter;
import com.vkm_backend.infra.global.specification.DynamicSpecification;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListGroupsUserservice {

    private final GroupsRepository groupsRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final GroupMapper groupMapper;
    private final PageResponseMapper pageResponseMapper;

    public ListGroupsUserservice(GroupsRepository groupsRepository, GroupMembersRepository groupMembersRepository, GroupMapper groupMapper, PageResponseMapper pageResponseMapper) {
        this.groupsRepository = groupsRepository;
        this.groupMembersRepository = groupMembersRepository;
        this.groupMapper = groupMapper;
        this.pageResponseMapper = pageResponseMapper;
    }

    public PageResponse<GroupResponse> getGroupUserService(GroupSearchRequest request, Pageable pageable, UserEntity user) {
        List<GroupMembersEntity> groupMembers = groupMembersRepository.findAll(DynamicSpecification.
                <GroupMembersEntity>where(DynamicFilter.toEquals(user, "userId")));

        if (groupMembers.isEmpty()) {
            return pageResponseMapper.toPageResponse(Page.empty(pageable), groupMapper);
        }

        List<GroupsEntity> groups = groupMembers.stream().map(GroupMembersEntity::getGroupId).toList();

        List<Long> groupIds = groups.stream().map(GroupsEntity::getId).toList();
        List<Long> groupIdsSearch = groupIds;

        if (request.id() != null) {
            if (!groupIds.contains(request.id())) {
                return pageResponseMapper.toPageResponse(Page.empty(pageable), groupMapper);
            }
            groupIdsSearch = List.of(request.id());
        }

        Page<GroupsEntity> groupsList = groupsRepository.findAll(DynamicSpecification.
                <GroupsEntity>where(DynamicFilter.toContains(groupIdsSearch, "id"))
                .and(DynamicFilter.toEquals(request.name(), "name"))
                .and(DynamicFilter.toEquals(request.description(), "description"))
                .and(DynamicFilter.toEquals(request.city(), "city"))
                .and(DynamicFilter.toEquals(request.state(), "state")), pageable);

        return pageResponseMapper.toPageResponse(groupsList, groupMapper);
    }
}
