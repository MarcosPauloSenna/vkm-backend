package com.vkm_backend.user.infra.web.controllers;


import com.vkm_backend.user.infra.web.dto.RefreshTokenRequest;
import com.vkm_backend.user.infra.web.dto.RefreshTokenResult;
import com.vkm_backend.user.service.AccessTokenService;
import com.vkm_backend.user.infra.web.dto.AuthenticationRequest;
import com.vkm_backend.user.infra.web.dto.AuthenticationResponse;
import com.vkm_backend.user.service.LogoutRequest;
import com.vkm_backend.user.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

        String tokenFamily = UUID.randomUUID().toString();
        var refreshToken = refreshTokenService.create(data.username(),tokenFamily);

        return ResponseEntity.ok(new AuthenticationResponse(accessTokens, refreshToken));

    }

    @GetMapping("/refresh")
    public ResponseEntity refresh(@Valid @RequestBody RefreshTokenRequest request){

        RefreshTokenResult newsTokens = refreshTokenService.refresh(request.refreshToken());
        return ResponseEntity.ok(new AuthenticationResponse(newsTokens.accessToken(), newsTokens.refreshToken()));

    }

    @GetMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody LogoutRequest request){
        refreshTokenService.logout(request.refreshToken());

    }
}
