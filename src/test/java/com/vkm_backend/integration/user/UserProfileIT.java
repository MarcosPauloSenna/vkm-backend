package com.vkm_backend.integration.user;

import com.vkm_backend.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserProfileIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldUpdateAuthenticatedUserProfile() throws Exception {
        String accessToken = createUserAndLogin(
                "profile1",
                "75999999997",
                "Profile Test",
                "12345678"
        );

        mockMvc.perform(
                        patch("/api/v1/user/me")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nome": "Profile Updated",
                                          "phone": "75999999998"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Profile Updated"))
                .andExpect(jsonPath("$.phone").value("75999999998"));
    }

    @Test
    void shouldUpdatePasswordAndAllowLoginWithNewPassword() throws Exception {
        createUser(
                "password1",
                "75919999999",
                "Password Test",
                "12345678"
        );
        String accessToken = login("password1", "12345678");

        mockMvc.perform(
                        patch("/api/v1/user/password")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "newPassword": "NovaSenha123"
                                        }
                                        """)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "password1",
                                          "password": "12345678"
                                        }
                                        """)
                )
                .andExpect(status().isUnauthorized());

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "password1",
                                          "password": "NovaSenha123"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void shouldRejectPhoneAlreadyUsedByAnotherUser() throws Exception {
        String firstToken = createUserAndLogin(
                "phoneuser1",
                "75999999990",
                "First Phone User",
                "12345678"
        );
        createUser(
                "phoneuser2",
                "75919999991",
                "Second Phone User",
                "12345678"
        );

        mockMvc.perform(
                        patch("/api/v1/user/me")
                                .header("Authorization", "Bearer " + firstToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "phone": "75919999991"
                                        }
                                        """)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT_EXCEPTION"));
    }

    private String createUserAndLogin(
            String username,
            String phone,
            String name,
            String password
    ) throws Exception {
        createUser(username, phone, name, password);
        return login(username, password);
    }

    private void createUser(
            String username,
            String phone,
            String name,
            String password
    ) throws Exception {
        mockMvc.perform(
                        post("/api/v1/user/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "%s",
                                          "birthDate": "01/01/1991",
                                          "phone": "%s",
                                          "profilePhoto": null,
                                          "username": "%s",
                                          "password": "%s"
                                        }
                                        """.formatted(name, phone, username, password))
                )
                .andExpect(status().isCreated());
    }

    private String login(String username, String password) throws Exception {
        String response = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "%s",
                                          "password": "%s"
                                        }
                                        """.formatted(username, password))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode tokens = objectMapper.readTree(response);
        assertThat(tokens.get("accessToken").asText()).isNotBlank();
        return tokens.get("accessToken").asText();
    }
}
