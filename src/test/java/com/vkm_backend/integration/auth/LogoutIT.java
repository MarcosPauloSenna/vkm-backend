package com.vkm_backend.integration.auth;

import com.vkm_backend.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LogoutIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRejectInvalidRefreshTokenOnLogout() throws Exception {

        mockMvc.perform(
                post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "refresh-token-invalido"
                                }
                                """)
        )
        .andExpect(status().is4xxClientError());
    }

    /*
     * Complete este teste quando o contrato final de logout estiver definido.
     */
    @Test
    void shouldRevokeRefreshTokenOnLogout() {
        // login -> refresh token -> logout -> refresh deve falhar
    }
}
