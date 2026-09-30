package com.vkm_backend.group.usecase;

import com.vkm_backend.group.usecase.GroupSortFields;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.specification.DynamicFilter;
import com.vkm_backend.infra.global.specification.DynamicSpecification;
import com.vkm_backend.group.domain.Groups;
import com.vkm_backend.group.infra.mapper.GroupMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.group.infra.persistence.web.dto.GroupSearchRequest;
import com.vkm_backend.user.infra.persistence.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class GroupFindUsecase {

    private final GroupsRepository groupsRepository;

    private final GroupMapper groupMapper;
    private final UserRepository userRepository;

    public GroupFindUsecase(GroupsRepository groupsRepository, GroupMapper groupMapper, UserRepository userRepository) {
        this.groupsRepository = groupsRepository;
        this.groupMapper = groupMapper;
        this.userRepository = userRepository;
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


        Page<Groups> pageDomain = groupsEntityList.map(groupMapper::toDomain);
        Page<GroupResponse> responsePage = pageDomain.map(groupMapper::toResponse);





        return new PageResponse<>(responsePage.getContent(),
                responsePage.getNumber(),
                responsePage.getSize(),
                responsePage.getNumberOfElements(),
                responsePage.getTotalPages(),
                responsePage.isFirst(),
                responsePage.isLast(),
                responsePage.hasNext(),
                responsePage.hasPrevious());

    }
}
