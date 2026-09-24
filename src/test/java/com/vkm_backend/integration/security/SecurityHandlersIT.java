package com.vkm_backend.integration.security;

import com.vkm_backend.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SecurityHandlersIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturn401WhenAccessingProtectedEndpointWithoutToken()
            throws Exception {

        mockMvc.perform(get("/api/v1/user/searchall"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldReturn403WhenAuthenticatedUserHasNoAdminRole()
            throws Exception {

        String accessToken = createUserAndLogin();

        mockMvc.perform(
                        get("/api/v1/user/searchall")
                                .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("ACCESS_DENIED"));
    }

    private String createUserAndLogin() throws Exception {
        mockMvc.perform(
                post("/api/v1/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Usuario Comum",
                                  "birthDate": "01/01/1991",
                                  "phone": "75999999992",
                                  "profilePhoto": null,
                                  "username": "user-403",
                                  "password": "12345678"
                                }
                                """)
        );

        String loginResponse = mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "user-403",
                                          "password": "12345678"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(loginResponse)
                .get("accessToken")
                .asText();
    }
}
