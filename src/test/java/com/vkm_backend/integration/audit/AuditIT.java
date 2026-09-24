package com.vkm_backend.integration.audit;

import com.vkm_backend.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuditIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldHaveAuditColumnsInUsersTable() {

        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.columns
                WHERE table_schema = 'public'
                  AND table_name = 'users'
                  AND column_name IN ('created_at', 'updated_at')
                """,
                Integer.class
        );

        assertThat(count).isEqualTo(2);
    }

    @Test
        void shouldPopulateAuditTimestampsWhenUserIsCreated() throws Exception {
        mockMvc.perform(
                post("/api/v1/user/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "name": "Audit Test",
                          "birthDate": "01/01/1991",
                          "phone": "75999999995",
                          "profilePhoto": null,
                          "username": "audituser",
                          "password": "12345678"
                        }
                        """)
            )
            .andExpect(status().isCreated());

        LocalDateTime[] timestamps = jdbcTemplate.queryForObject(
            """
            SELECT created_at, updated_at
            FROM users
            WHERE username = ?
            """,
            (resultSet, rowNum) -> new LocalDateTime[]{
                resultSet.getObject("created_at", LocalDateTime.class),
                resultSet.getObject("updated_at", LocalDateTime.class)
            },
            "audituser"
        );

        assertThat(timestamps).isNotNull();
        assertThat(timestamps[0]).isNotNull();
        assertThat(timestamps[1]).isNotNull();
        assertThat(timestamps[1]).isAfterOrEqualTo(timestamps[0]);
    }

        @Test
        void shouldHaveAuditActorColumnsInUsersTable() {
        Integer count = jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = 'users'
              AND column_name IN ('created_by', 'updated_by')
            """,
            Integer.class
        );

        assertThat(count).isEqualTo(2);
        }

        @Test
        void shouldPopulateUpdatedByWhenAuthenticatedUserUpdatesProfile() throws Exception {
        mockMvc.perform(
                post("/api/v1/user/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "name": "Audit Actor Test",
                          "birthDate": "01/01/1991",
                          "phone": "75999999996",
                          "profilePhoto": null,
                          "username": "auditactor",
                          "password": "12345678"
                        }
                        """)
            )
            .andExpect(status().isCreated());

        Map<String, Object> createdUser = jdbcTemplate.queryForMap(
            "SELECT id, created_by, updated_by FROM users WHERE username = ?",
            "auditactor"
        );
        long userId = ((Number) createdUser.get("id")).longValue();

        String loginResponse = mockMvc.perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "username": "auditactor",
                          "password": "12345678"
                        }
                        """)
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        JsonNode login = objectMapper.readTree(loginResponse);

        mockMvc.perform(
                patch("/api/v1/user/me")
                    .header("Authorization", "Bearer " + login.get("accessToken").asText())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "nome": "Audit Actor Updated"
                        }
                        """)
            )
            .andExpect(status().isOk());

        Map<String, Object> updatedUser = jdbcTemplate.queryForMap(
            "SELECT created_by, updated_by FROM users WHERE id = ?",
            userId
        );

        assertThat(updatedUser.get("created_by")).isNull();
        assertThat(((Number) updatedUser.get("updated_by")).longValue()).isEqualTo(userId);
        }
}
