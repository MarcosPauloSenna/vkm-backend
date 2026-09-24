package com.vkm_backend.integration.user;

import com.vkm_backend.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CreateUserIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateUser() throws Exception {

        mockMvc.perform(
                post("/api/v1/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "João Silva",
                                  "birthDate": "01/01/1991",
                                  "phone": "75999999999",
                                  "profilePhoto": null,
                                  "username": "joao",
                                  "password": "12345678"
                                }
                                """)
        )
        .andExpect(status().isCreated());
    }

    @Test
    void shouldRejectInvalidRequest() throws Exception {

        mockMvc.perform(
                post("/api/v1/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "birthDate": null,
                                  "phone": "",
                                  "username": "",
                                  "password": ""
                                }
                                """)
        )
        .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldRejectDuplicatedUsername() throws Exception {

        String body = """
                {
                  "name": "João Silva",
                  "birthDate": "1990-01-01",
                  "phone": "75999999999",
                  "profilePhoto": null,
                  "username": "joao",
                  "password": "123456"
                }
                """;

        mockMvc.perform(
                post("/api/v1/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        );

        mockMvc.perform(
                post("/api/v1/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        )
        .andExpect(status().is4xxClientError());
    }
}
