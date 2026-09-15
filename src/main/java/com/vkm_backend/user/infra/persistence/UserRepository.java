package com.vkm_backend.user.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<UserEntity, Long> {


    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    UserDetails findByUsername(String username);
}
