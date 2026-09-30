package com.vkm_backend.user.infra.persistence;

import com.vkm_backend.infra.global.specification.DynamicRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserRepository extends DynamicRepository<UserEntity, Long> {


    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    UserEntity findByUsername(String username);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    UserEntity getReferenceById(Long id);

    List<UserEntity> findByName(String name);
}
