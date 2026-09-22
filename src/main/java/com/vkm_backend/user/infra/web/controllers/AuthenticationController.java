package com.vkm_backend.user.infra.web.controllers;


import com.vkm_backend.infra.global.handler.ErrorResponse;
import com.vkm_backend.user.infra.web.dto.AuthenticationRequest;
import com.vkm_backend.user.infra.web.dto.AuthenticationResponse;
import com.vkm_backend.user.infra.web.dto.RefreshTokenRequest;
import com.vkm_backend.user.infra.web.dto.RefreshTokenResult;
import com.vkm_backend.user.service.AccessTokenService;
import com.vkm_backend.user.service.LogoutRequest;
import com.vkm_backend.user.service.RefreshTokenService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticação", description = "Operações de login, renovação e encerramento de sessões")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
        @Operation(
            summary = "Autenticar usuário",
            description = "Valida as credenciais do usuário e retorna um access token JWT e um refresh token.",
            tags = {"Autenticação"}
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados de login inválidos",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuário ou senha inválidos",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuário não encontrado ou inativo",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
    public ResponseEntity login(@Valid @RequestBody AuthenticationRequest data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var accessTokens = accessTokenService.generateAccessTokens((UserDetails)auth.getPrincipal());

        String tokenFamily = UUID.randomUUID().toString();
        var refreshToken = refreshTokenService.create(data.username(),tokenFamily);

        return ResponseEntity.ok(new AuthenticationResponse(accessTokens, refreshToken));

    }

    @PostMapping("/refresh")
        @Operation(
            summary = "Renovar tokens de acesso",
            description = "Valida o refresh token, revoga o token atual e retorna um novo par de tokens.",
            tags = {"Autenticação"}
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tokens renovados com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Refresh token não informado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido, expirado ou revogado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
    public ResponseEntity refresh(@Valid @RequestBody RefreshTokenRequest request){

        RefreshTokenResult newTokens = refreshTokenService.refresh(request.refreshToken());
        return ResponseEntity.ok(new AuthenticationResponse(newTokens.accessToken(), newTokens.refreshToken()));

    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
        @Operation(
            summary = "Encerrar sessão",
            description = "Revoga o refresh token informado e encerra a sessão correspondente.",
            tags = {"Autenticação"}
        )
        @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sessão encerrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Refresh token inválido no corpo da requisição",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
    public void logout(@RequestBody LogoutRequest request){
        refreshTokenService.logout(request.refreshToken());

    }
}
