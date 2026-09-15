package com.vkm_backend.user.usecase;

import com.vkm_backend.global.exceptions.BusinessException;
import com.vkm_backend.user.dominio.User;
import com.vkm_backend.user.infra.mapper.UserMapper;
import com.vkm_backend.user.infra.persistence.UserEntity;
import com.vkm_backend.user.infra.persistence.UserRepository;
import com.vkm_backend.user.infra.web.CreateUserRequest;
import com.vkm_backend.user.infra.web.UserResponse;
import org.springframework.stereotype.Service;

@Service
public class UserUseCase {


    private final UserRepository userRepository;

    private   final  UserMapper userMapper;

    public UserUseCase(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;

        this.userMapper = userMapper;
    }

    public UserResponse createUser( CreateUserRequest request){

        User user = userMapper.fromUser(request);
        UserEntity entity =  userMapper.toEntity(user);
        // validando Username e Phone
        if (userRepository.existsByUsername(entity.getUsername())){
            throw new BusinessException("Nome de usuario: " +entity.getUsername()+ ", ja existe. Digite outro nome de usuario! ", 300);
        }else if (userRepository.existsByPhone(entity.getPhone())) {
            throw new BusinessException("Telefone: " +entity.getUsername()+ ", ja existe. Digite outro Telefone! ", 301);
        };

        UserEntity userCreated = userRepository.save(entity);


        return userMapper.toResponse(userMapper.toDomain(userCreated));

    }
}
