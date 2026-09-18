package com.vkm_backend.user.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class AccessTokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration-minutes}")
    private Integer expirationMinutes;


    public String generateAccessTokens(UserDetails user){

        Algorithm algorithm = Algorithm.HMAC256(secret);
        String token = JWT.create()
                .withIssuer("auth-api")
                .withSubject(user.getUsername())
                .withExpiresAt(generateExpiretionDate())
                .sign(algorithm);
        return token;
    }

    public String validationAccessToken(String token){
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm)
                .withIssuer("auth-api")
                .build()
                .verify(token)
                .getSubject();
    }

    private Instant generateExpiretionDate(){
        return LocalDateTime.now().plusMinutes(expirationMinutes).toInstant(ZoneOffset.of("-03:00"));
    }
}
