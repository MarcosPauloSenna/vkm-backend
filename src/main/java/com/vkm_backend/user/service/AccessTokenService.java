package com.vkm_backend.user.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vkm_backend.infra.global.handler.ErrorResponse;
import com.vkm_backend.infra.security.exception.TokenValidationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Service
public class AccessTokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration-minutes}")
    private Integer expirationMinutes;

    private  final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");


    public String generateAccessTokens(UserDetails user){

        Algorithm algorithm = Algorithm.HMAC256(secret);
        String token = JWT.create()
                .withIssuer("auth-api")
                .withSubject(user.getUsername())
                .withExpiresAt(generateExpirationDate())
                .sign(algorithm);
        return token;
    }

    public TokenValidationResult validationAccessToken(String token) {

        try {

            Algorithm algorithm = Algorithm.HMAC256(secret);

            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token);

            return TokenValidationResult.valid(
                    decodedJWT.getSubject()
            );

        } catch (TokenExpiredException e) {

            return TokenValidationResult.expired(
                    new ErrorResponse(
                            LocalDateTime.now(ZONE),
                            401,
                            "Token de acesso expirado.",
                            "TOKEN_EXPIRED"
                    )
            );

        } catch (JWTVerificationException e) {

            return TokenValidationResult.invalid(
                    new ErrorResponse(
                            LocalDateTime.now(ZONE),
                            401,
                            "Token de acesso inválido.",
                            "TOKEN_INVALID"
                    )
            );
        }
    }

    private Instant generateExpirationDate(){
        return LocalDateTime.now().plusMinutes(expirationMinutes).toInstant(ZoneOffset.of("-03:00"));
    }
}
