package com.vkm_backend.user.infra.web.controllers;

import com.vkm_backend.user.infra.web.dto.CreateUserRequest;
import com.vkm_backend.user.infra.web.dto.UserResponse;
import com.vkm_backend.user.usecase.UserUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private  final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {

        this.userUseCase = userUseCase;
    }

    @PostMapping("/create")
    public UserResponse saveUser(@Valid @RequestBody CreateUserRequest request){

        return  userUseCase.createUser(request);
    }

    @GetMapping("/serchall")
    public List<UserResponse> findAllUsers(){

        return userUseCase.listAllUsers();

    }





}
