package com.vkm_backend.integration.migration;

import com.vkm_backend.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FlywayMigrationIT extends AbstractIntegrationTest {

    @Test
    void shouldExecuteAllFlywayMigrationsSuccessfully() {
        Integer failed = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM flyway_schema_history
                WHERE success = false
                """,
                Integer.class
        );

        assertThat(failed).isZero();
    }

    @Test
    void shouldHaveFlywayHistory() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM flyway_schema_history
                """,
                Integer.class
        );

        assertThat(count).isGreaterThan(0);
    }

    @Test
    void shouldCreateUsersTable() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'users'
                """,
                Integer.class
        );

        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldCreateRefreshTokensTable() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'refresh_tokens'
                """,
                Integer.class
        );

        assertThat(count).isEqualTo(1);
    }
}
