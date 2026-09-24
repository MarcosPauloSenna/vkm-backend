package com.vkm_backend.integration.auth;

import com.vkm_backend.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RefreshTokenIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    void shouldRefreshAccessToken() throws Exception {

        JsonNode loginTokens = createUserAndLogin();
        String oldRefreshToken = loginTokens.get("refreshToken").asText();

        String refreshResponse = mockMvc.perform(
                        post("/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "refreshToken": "%s"
                                        }
                                        """.formatted(oldRefreshToken))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode newTokens = objectMapper.readTree(refreshResponse);

        // refresh token é rotacionado a cada uso; o antigo deve ser revogado
        assertThat(newTokens.get("refreshToken").asText())
                .isNotEqualTo(oldRefreshToken);

        mockMvc.perform(
                        post("/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "refreshToken": "%s"
                                        }
                                        """.formatted(oldRefreshToken))
                )
                .andExpect(status().isUnauthorized());
    }


    private JsonNode createUserAndLogin() throws Exception {
        mockMvc.perform(
                post("/api/v1/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Refresh Test",
                                  "birthDate": "01/01/1991",
                                  "phone": "75999999993",
                                  "profilePhoto": null,
                                  "username": "refresh1",
                                  "password": "12345678"
                                }
                                """)
        );

        String loginResponse = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "refresh1",
                                          "password": "12345678"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(loginResponse);
    }
}
