package com.vkm_backend.unit.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.vkm_backend.infra.security.exception.TokenValidationResult;
import com.vkm_backend.user.service.AccessTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class AccessTokenServiceTest {

    private static final String SECRET = "secret-secret-secret-secret-secret";

    private AccessTokenService service;

    @BeforeEach
    void setUp() {
        service = new AccessTokenService();

        ReflectionTestUtils.setField(service, "secret", SECRET);
        ReflectionTestUtils.setField(service, "expirationMinutes", 30);
    }

    @Test
    void shouldValidateValidToken() {

        String token = JWT.create()
                .withIssuer("auth-api")
                .withSubject("marcos")
                .withExpiresAt(Date.from(
                        Instant.now().plusSeconds(3600)
                ))
                .sign(Algorithm.HMAC256(SECRET));

        TokenValidationResult result =
                service.validationAccessToken(token);

        assertThat(result.status())
                .isEqualTo(TokenValidationResult.Status.VALID);

        assertThat(result.username())
                .isEqualTo("marcos");
    }

    @Test
    void shouldReturnExpiredForExpiredToken() {

        String token = JWT.create()
                .withIssuer("auth-api")
                .withSubject("marcos")
                .withExpiresAt(Date.from(
                        Instant.now().minusSeconds(60)
                ))
                .sign(Algorithm.HMAC256(SECRET));

        TokenValidationResult result =
                service.validationAccessToken(token);

        assertThat(result.status())
                .isEqualTo(TokenValidationResult.Status.EXPIRED);

        assertThat(result.username())
                .isNull();
    }

    @Test
    void shouldReturnInvalidForMalformedToken() {

        TokenValidationResult result =
                service.validationAccessToken("token-invalido");

        assertThat(result.status())
                .isEqualTo(TokenValidationResult.Status.INVALID);

        assertThat(result.username())
                .isNull();
    }

    @Test
    void shouldReturnInvalidForWrongSignature() {

        String token = JWT.create()
                .withIssuer("auth-api")
                .withSubject("marcos")
                .withExpiresAt(Date.from(
                        Instant.now().plusSeconds(3600)
                ))
                .sign(Algorithm.HMAC256("outra-chave"));

        TokenValidationResult result =
                service.validationAccessToken(token);

        assertThat(result.status())
                .isEqualTo(TokenValidationResult.Status.INVALID);
    }

    @Test
    void shouldReturnInvalidForWrongIssuer() {

        String token = JWT.create()
                .withIssuer("outro-issuer")
                .withSubject("marcos")
                .withExpiresAt(Date.from(
                        Instant.now().plusSeconds(3600)
                ))
                .sign(Algorithm.HMAC256(SECRET));

        TokenValidationResult result =
                service.validationAccessToken(token);

        assertThat(result.status())
                .isEqualTo(TokenValidationResult.Status.INVALID);
    }
}
