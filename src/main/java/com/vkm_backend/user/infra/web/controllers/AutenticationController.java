package com.vkm_backend.user.infra.web.controllers;


import com.vkm_backend.user.service.TokenService;
import com.vkm_backend.user.infra.web.dto.AuthenticationRequest;
import com.vkm_backend.user.infra.web.dto.AuthenticationResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AutenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody AuthenticationRequest data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateTokens((UserDetails)auth.getPrincipal());

        return ResponseEntity.ok(new AuthenticationResponse(token));

    }
}
