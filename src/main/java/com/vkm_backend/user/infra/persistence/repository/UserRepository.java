package com.vkm_backend.user.infra.persistence.repository;

import com.vkm_backend.infra.global.specification.DynamicRepository;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;

import java.util.List;


public interface UserRepository extends DynamicRepository<UserEntity, Long> {


    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    UserEntity findByUsername(String username);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    UserEntity getReferenceById(Long id);

    List<UserEntity> findByName(String name);
}
