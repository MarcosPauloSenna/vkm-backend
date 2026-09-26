package com.vkm_backend.group.infra.mapper;

import com.vkm_backend.group.domain.Groups;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.web.dto.CreateGroupRequest;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {

    public GroupsEntity toEntity(Groups domain) {
        GroupsEntity entity = new GroupsEntity();

        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setCity(domain.getCity());
        entity.setState(domain.getState());
        entity.setActive(domain.getActive());
        return entity;

    }

    public Groups toDomain(GroupsEntity entity) {
        Groups groups = new Groups();

        groups.setId(entity.getId());
        groups.setName(entity.getName());
        groups.setDescription(entity.getDescription());
        groups.setCity(entity.getCity());
        groups.setState(entity.getState());
        groups.setActive(entity.getActive());
        groups.setCreatedBy(entity.getCreatedBy());
        groups.setUpdatedBy(entity.getUpdatedBy());
        groups.setCreatedAt(entity.getCreatedAt());
        groups.setUpdatedAt(entity.getUpdatedAt());
        return groups;
    }

    public Groups toDomain(CreateGroupRequest request) {
        Groups groups = new Groups();

        groups.setName(request.name());
        groups.setDescription(request.description());
        groups.setCity(request.city());
        groups.setState(request.state());
        return groups;

    }

    public GroupResponse toResponse(Groups domain) {

        return new GroupResponse(domain.getId(),
                domain.getName(),
                domain.getDescription(),
                domain.getCity(),
                domain.getState(),
                domain.getActive().name());

    }


}
