package com.vkm_backend.user.usecase;

import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.ConflictException;
import com.vkm_backend.infra.global.exceptions.ResourceNotFoundException;
import com.vkm_backend.user.domain.User;
import com.vkm_backend.user.infra.mapper.UserMapper;
import com.vkm_backend.user.infra.persistence.UserEntity;
import com.vkm_backend.user.infra.persistence.UserRepository;
import com.vkm_backend.user.infra.web.dto.CreateUserRequest;
import com.vkm_backend.user.infra.web.dto.UpdateUserRequest;
import com.vkm_backend.user.infra.web.dto.UserResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
            throw new BusinessException("Nome de usuario: " +entity.getUsername()+ ", já esta cadastrado. Digite outro nome de usuario! ");
        }else if (userRepository.existsByPhone(entity.getPhone())) {
            throw new BusinessException("Telefone: " +entity.getPhone()+ ", já esta cadastrado. Digite outro Telefone! ");
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


    public UserResponse getMe(String userNamer){
        UserEntity user = userRepository.findByUsername(userNamer);
        if (user == null) {
            throw new ResourceNotFoundException(" Usuario não localizado! ");
        }
        return  userMapper.toResponse(userMapper.toDomain(user));
    }

    @Transactional
    public UserResponse updateMe(UpdateUserRequest user, String username){
        UserEntity entity = userRepository.findByUsername(username);
        if (entity == null) {
            throw new ResourceNotFoundException(" Usuario não localizado! ");
        }
            if (user.nome() == null
                && user.birthDate() == null
                && user.phone() == null
                && user.profilePhoto() == null) {

            throw new BusinessException(
                    "Nenhum dado informado para atualização.");
        }

        if (user.nome() != null){
            entity.setName(user.nome());
        }

        if (user.birthDate() != null){
            entity.setBirthDate(user.birthDate());
        }


        if (user.phone() != null){
            if (userRepository.existsByPhoneAndIdNot(user.phone(), entity.getId())){

                throw new ConflictException("O telefone informado já está cadastrado.");

            }
            entity.setPhone(user.phone());
        }

        if (user.profilePhoto() != null){
            entity.setProfilePhoto(user.profilePhoto());
        }

        UserEntity userUpdated = userRepository.save(entity);

        return userMapper.toResponse(userMapper.toDomain(userUpdated));
    }

    @Transactional
    public UserResponse updatePassword(String newPassword, String username){
        UserEntity entity = userRepository.findByUsername(username);

        if (entity == null){
            throw new ResourceNotFoundException(" Usuario não localizado! ");
        }
        if (newPassword != null){
            entity.setPassword(passwordEncoder.encode(newPassword));
        }else {
            throw new BusinessException(
                    "Nenhum dado informado para atualização.");
        }

        UserEntity userPasswordUpdated = userRepository.save(entity);

        return userMapper.toResponse(userMapper.toDomain(userPasswordUpdated));
    }
}
