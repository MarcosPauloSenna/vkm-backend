package com.vkm_backend.integration.security;

import com.vkm_backend.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InvalidJwtIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn401ForMalformedJwt() throws Exception {
        mockMvc.perform(
                get("/api/v1/user/searchall")
                        .header("Authorization", "Bearer token-invalido")
        )
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void shouldReturn401ForInvalidSignature() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9."
                + "eyJzdWIiOiJ1c2VyIn0."
                + "assinatura-invalida";

        mockMvc.perform(
                get("/api/v1/user/searchall")
                        .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401));
    }
}
