package com.vkm_backend.user.usecase;

import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.user.dominio.User;
import com.vkm_backend.user.infra.mapper.UserMapper;
import com.vkm_backend.user.infra.persistence.UserEntity;
import com.vkm_backend.user.infra.persistence.UserRepository;
import com.vkm_backend.user.infra.web.dto.CreateUserRequest;
import com.vkm_backend.user.infra.web.dto.UserResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserUseCase {


    private final UserRepository userRepository;

    private   final  UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public UserUseCase(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;

        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUser( CreateUserRequest request){

        User user = userMapper.fromUser(request);
        UserEntity entity =  userMapper.toEntity(user);
        // validando Username e Phone
        if (userRepository.existsByUsername(entity.getUsername())){
            throw new BusinessException("Nome de usuario: " +entity.getUsername()+ ", já esta cadastrado. Digite outro nome de usuario! ", 300);
        }else if (userRepository.existsByPhone(entity.getPhone())) {
            throw new BusinessException("Telefone: " +entity.getPhone()+ ", já esta cadastrado. Digite outro Telefone! ", 301);
        };

        //Criando hash password
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));

        UserEntity userCreated = userRepository.save(entity);


        return userMapper.toResponse(userMapper.toDomain(userCreated));

    }


    public List<UserResponse> listAllUsers(){
         List<UserResponse> users = new ArrayList<>();

         List<UserEntity> usersEnt = userRepository.findAll();

         for (UserEntity userEnt: usersEnt){
             User user = userMapper.toDomain(userEnt);
             users.add(userMapper.toResponse(user));
         }

         return users;
    }
}
