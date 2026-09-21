package com.vkm_backend.user.infra.web.controllers;

import com.vkm_backend.user.infra.web.dto.CreateUserRequest;
import com.vkm_backend.user.infra.web.dto.UpdatePasswordRequest;
import com.vkm_backend.user.infra.web.dto.UpdateUserRequest;
import com.vkm_backend.user.infra.web.dto.UserResponse;
import com.vkm_backend.user.usecase.UserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private  final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {

        this.userUseCase = userUseCase;
    }

    @PostMapping("/create")
    public UserResponse saveUser(@Valid @RequestBody CreateUserRequest request){

        return  userUseCase.createUser(request);
    }

    @GetMapping("/searchall")
    public List<UserResponse> findAllUsers(){

        return userUseCase.listAllUsers();

    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Authentication authentication){


        return ResponseEntity.ok(userUseCase.getMe(authentication.getName()));

    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody UpdateUserRequest user,Authentication authentication){

        return ResponseEntity.ok(userUseCase.updateMe(user, authentication.getName()));

    }


    @PatchMapping("/password")
    public  ResponseEntity<UserResponse> updatePassword(@Valid @RequestBody
                                                            UpdatePasswordRequest request,
                                                            Authentication authentication){

        return ResponseEntity.ok(userUseCase.updatePassword(request.newPassword(), authentication.getName()));
    }




}
