package com.vkm_backend.user.infra.mapper;


import com.vkm_backend.user.dominio.User;
import com.vkm_backend.user.infra.persistence.UserEntity;
import com.vkm_backend.user.infra.web.dto.CreateUserRequest;
import com.vkm_backend.user.infra.web.dto.UserResponse;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;

@Component
public class UserMapper {


    public UserEntity toEntity(User user){
        UserEntity entity = new UserEntity();

        if(user.getId() != null){
            entity.setId(user.getId());
            entity.setUpdatedAt(LocalDateTime.now());
        }

        entity.setName(user.getName());
        entity.setBirthDate(user.getBirthDate());
        entity.setPhone(user.getPhone());
        entity.setRole(user.getRole());
        entity.setActive(user.getActive());

        if (user.getProfilePhoto() != null) {
            entity.setProfilePhoto(user.getProfilePhoto());
        }
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());

        return entity;
    }

    public User toDomain(UserEntity entity){
        User user = new User();


        user.setId(entity.getId());
        user.setUpdatedAt(entity.getUpdatedAt());
        user.setName(entity.getName());
        user.setBirthDate(entity.getBirthDate());
        user.setPhone(entity.getPhone());
        if (entity.getProfilePhoto() != null) {
            user.setProfilePhoto(entity.getProfilePhoto());
        }
        user.setUsername(entity.getUsername());
        user.setPassword(entity.getPassword());

        return user;
    }

    public UserResponse toResponse(User user){

    return new UserResponse(user.getName(),
                                 user.getBirthDate(),
                                 user.getPhone(),
                                 user.getUsername());
    }

    public User fromUser(CreateUserRequest request){
        User user = new User();

        user.setName(request.name());
        user.setBirthDate(request.birthDate());
        user.setPhone(request.phone());
        user.setProfilePhoto(request.profilePhoto());
        user.setUsername(request.username());
        user.setPassword(request.password());

        return user;

    }

    public CreateUserRequest toRequest(User user){
        return  new CreateUserRequest(user.getName(),
                user.getBirthDate(), user.getPhone(), user.getProfilePhoto(), user.getUsername(), user.getPassword());
    }


}
