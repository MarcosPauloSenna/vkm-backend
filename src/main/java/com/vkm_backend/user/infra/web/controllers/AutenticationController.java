package com.vkm_backend.user.infra.web.controllers;


import com.vkm_backend.user.service.AccessTokenService;
import com.vkm_backend.user.infra.web.dto.AuthenticationRequest;
import com.vkm_backend.user.infra.web.dto.AuthenticationResponse;
import com.vkm_backend.user.service.RefreshTokenService;
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
    private AccessTokenService accessTokenService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody AuthenticationRequest data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var accessTokens = accessTokenService.generateAccessTokens((UserDetails)auth.getPrincipal());
        var refreshToken = refreshTokenService.create(data.username());

        return ResponseEntity.ok(new AuthenticationResponse(accessTokens, refreshToken));

    }
}
