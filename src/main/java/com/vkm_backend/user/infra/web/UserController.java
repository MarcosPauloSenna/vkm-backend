package com.vkm_backend.user.infra.web;

import com.vkm_backend.user.usecase.UserUseCase;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private  final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping("/create")
    public UserResponse saveUser(@RequestBody CreateUserRequest request){
        return  userUseCase.createUser(request);
    }





}
