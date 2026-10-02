package com.vkm_backend.group.usecase;

import com.vkm_backend.group.service.GetGroupsFromEntityToDomain;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.specification.DynamicFilter;
import com.vkm_backend.infra.global.specification.DynamicSpecification;
import com.vkm_backend.group.infra.mapper.GroupMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.group.infra.persistence.web.dto.GroupSearchRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class GroupFindUseCase {

    private final GroupsRepository groupsRepository;
    private final GetGroupsFromEntityToDomain fromEntityToDomain;
    private final GroupMapper groupMapper;


    public GroupFindUseCase(GroupsRepository groupsRepository, GetGroupsFromEntityToDomain fromEntityToDomain, GroupMapper groupMapper) {
        this.groupsRepository = groupsRepository;
        this.fromEntityToDomain = fromEntityToDomain;
        this.groupMapper = groupMapper;

    }

    @Transactional
    public PageResponse<GroupResponse> search(GroupSearchRequest request, Pageable pageable ) {

        pageable = SortWhitelist.validate(
                pageable,
                GroupSortFields.SORT_FIELDS
        );

        Page<GroupsEntity> groupsEntityList = groupsRepository.findAll(DynamicSpecification.<GroupsEntity>where(DynamicFilter.toEquals(request.id(), "id"))
                .and(DynamicFilter.toLike(request.name(), "name"))
                .and(DynamicFilter.toLike(request.description(), "description"))
                .and(DynamicFilter.toLike(request.city(), "city"))
                .and(DynamicFilter.toLike(request.state(), "state")),pageable);


        return fromEntityToDomain.execute(groupsEntityList, groupMapper);

    }
}
