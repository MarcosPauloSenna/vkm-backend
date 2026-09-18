package com.vkm_backend.user.service;

import com.vkm_backend.user.infra.persistence.UserEntity;
import com.vkm_backend.user.infra.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutorizationService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = repository.findByUsername(username);

        if (user == null){
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        return user;

    }
}
