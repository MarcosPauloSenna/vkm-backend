package com.vkm_backend.integration.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.vkm_backend.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ExpiredJwtIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${api.security.token.secret}")
    private String secret;

    @Test
    void shouldReturn401ForExpiredJwt() throws Exception {

        String token = JWT.create()
                .withIssuer("auth-api")
                .withSubject("usuario-teste")
                .withExpiresAt(Date.from(Instant.now().minusSeconds(60)))
                .sign(Algorithm.HMAC256(secret));

        mockMvc.perform(
                get("/api/v1/user/searchall")
                        .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401));
    }
}
