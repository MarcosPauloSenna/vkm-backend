package com.vkm_backend.integration.security;

import com.vkm_backend.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SecurityHandlersIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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

        // Requer usuário/token de teste conforme a sua implementação.
        // Este teste é habilitado quando o helper de login estiver configurado.
    }
}
