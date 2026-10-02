package com.vkm_backend.group.usecase;

import com.vkm_backend.group.infra.mapper.GroupMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.group.infra.persistence.web.dto.GroupSearchRequest;
import com.vkm_backend.infra.global.mapper.PageResponseMapper;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.exceptions.ValidationException;
import com.vkm_backend.infra.global.specification.DynamicFilter;
import com.vkm_backend.infra.global.specification.DynamicSpecification;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import com.vkm_backend.user.infra.persistence.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ListMyGroupsUseCase {

    private final UserRepository userRepository;
    private final GroupsRepository groupsRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final GroupMapper groupMapper;
    private final PageResponseMapper pageResponseMapper;

    public ListMyGroupsUseCase(UserRepository userRepository, GroupsRepository groupsRepository, GroupMembersRepository groupMembersRepository, GroupMapper groupMapper, PageResponseMapper pageResponseMapper) {
        this.userRepository = userRepository;
        this.groupsRepository = groupsRepository;
        this.groupMembersRepository = groupMembersRepository;
        this.groupMapper = groupMapper;
        this.pageResponseMapper = pageResponseMapper;
    }


    @Transactional
    public PageResponse<GroupResponse> execute(GroupSearchRequest request, String username, Pageable pageable) {
        pageable = SortWhitelist.validate(
                pageable,
                GroupSortFields.SORT_FIELDS
        );

        UserEntity user = userRepository.findByUsername(username);

        if (user == null) {
            throw new ValidationException("Usuario não localizado");
        }

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
