package com.vkm_backend.integration.auth;

import com.vkm_backend.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RefreshTokenIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRejectInvalidRefreshToken() throws Exception {

        mockMvc.perform(
                post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "refresh-token-invalido"
                                }
                                """)
        )
        .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldRejectMissingRefreshToken() throws Exception {

        mockMvc.perform(
                post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {}
                                """)
        )
        .andExpect(status().is4xxClientError());
    }

    /*
     * Complete este teste quando o DTO/endpoint final de refresh estiver
     * definido no projeto.
     */
    @Test
    void shouldRefreshAccessToken() {
        // login -> refresh token -> POST /auth/refresh -> novo access token
    }
}
