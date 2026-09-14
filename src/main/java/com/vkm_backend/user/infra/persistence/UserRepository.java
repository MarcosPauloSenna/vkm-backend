package com.vkm_backend.user.infra.persistence;

import com.vkm_backend.user.dominio.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


}
