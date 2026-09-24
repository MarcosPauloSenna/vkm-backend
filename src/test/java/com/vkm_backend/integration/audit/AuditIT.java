package com.vkm_backend.integration.audit;

import com.vkm_backend.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuditIT extends AbstractIntegrationTest {

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
    void shouldPopulateAuditTimestampsWhenUserIsCreated() {
        // Implementar após confirmar o mecanismo de auditoria:
        // @CreatedDate / @LastModifiedDate ou listener próprio.
    }
}
