package com.vkm_backend.user.infra.persistence;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserRepository extends JpaRepository<UserEntity, Long> {


    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    UserEntity findByUsername(String username);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    UserEntity getReferenceById(Long id);
}
