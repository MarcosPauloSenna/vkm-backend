package com.vkm_backend.group.infra.persistence.repository;

import com.vkm_backend.group.domain.Groups;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupsRepository extends JpaRepository<GroupsEntity, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndCityAndState(String name, String city, String state);

    GroupsEntity getReferenceById(Long id);
}
