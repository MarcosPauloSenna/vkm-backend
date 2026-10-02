package com.vkm_backend.group.service;

import com.vkm_backend.group.domain.Groups;
import com.vkm_backend.group.infra.mapper.GroupMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.infra.global.dto.PageResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;



@Service
public class GetGroupsFromEntityToDomain {


    @NonNull
    public PageResponse<GroupResponse> execute(Page<GroupsEntity> groupsList, GroupMapper groupMapper) {
        Page<Groups> myGroupsList = groupsList.map(groupMapper::toDomain);

        Page<GroupResponse> responsePage = myGroupsList.map(groupMapper::toResponse);

        return new PageResponse<>(responsePage.getContent(),
                responsePage.getNumber(),
                responsePage.getSize(),
                responsePage.getTotalElements(),
                responsePage.getTotalPages(),
                responsePage.isFirst(),
                responsePage.isLast(),
                responsePage.hasNext(),
                responsePage.hasPrevious());
    }
}
