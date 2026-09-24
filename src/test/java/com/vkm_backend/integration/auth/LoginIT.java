package com.vkm_backend.integration.auth;

import com.vkm_backend.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LoginIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldLoginWithValidCredentials() throws Exception {

        createUser();

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "login-test",
                                  "password": "12345678"
                                }
                                """)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void shouldRejectInvalidPassword() throws Exception {

        createUser();

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "login-test",
                                  "password": "senha-errada"
                                }
                                """)
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectUnknownUser() throws Exception {

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "nao-existe",
                                  "password": "12345678"
                                }
                                """)
        )
        .andExpect(status().isUnauthorized());
    }

    private void createUser() throws Exception {
        mockMvc.perform(
                post("/api/v1/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Login Test",
                                  "birthDate": "01/01/1991",
                                  "phone": "75999999991",
                                  "profilePhoto": null,
                                  "username": "login-test",
                                  "password": "12345678"
                                }
                                """)
        );
    }
}
