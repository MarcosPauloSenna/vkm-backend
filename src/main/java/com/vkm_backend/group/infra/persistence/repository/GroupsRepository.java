package com.vkm_backend.group.infra.persistence.repository;

import com.vkm_backend.infra.global.specification.DynamicRepository;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;

public interface GroupsRepository extends DynamicRepository<GroupsEntity, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndCityAndState(String name, String city, String state);

    GroupsEntity getReferenceById(Long id);
}
