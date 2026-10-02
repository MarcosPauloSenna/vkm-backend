package com.vkm_backend.integration.migration;

import com.vkm_backend.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void shouldCreateGroupsAndGroupMembersTables() {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name IN ('groups', 'group_members')
                """,
                Integer.class
        );

        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldEnforceGroupMemberUniquenessAndChecks() {
        jdbcTemplate.update("""
                INSERT INTO users (name, birth_date, phone, username, password, created_at, updated_at)
                VALUES ('Mig User', DATE '1990-01-01', '75000000001', 'miguser1', 'x', now(), now())
                """);
        Long userId = jdbcTemplate.queryForObject("SELECT id FROM users WHERE username = 'miguser1'", Long.class);
        jdbcTemplate.update("""
                INSERT INTO groups (name, description, city, state, active, owner_id, created_at, updated_at)
                VALUES ('Mig Group', 'd', 'c', 's', 1, ?, now(), now())
                """, userId);
        Long groupId = jdbcTemplate.queryForObject("SELECT id FROM groups WHERE name = 'Mig Group'", Long.class);

        for (String status : new String[]{"APPROVED", "PENDING", "REJECTED", "SUSPENDED", "CANCELLED", "LEFT"}) {
            jdbcTemplate.update("DELETE FROM group_members WHERE group_id = ?", groupId);
            jdbcTemplate.update("""
                    INSERT INTO group_members (group_id, user_id, role, status, created_at, updated_at)
                    VALUES (?, ?, 'MEMBER', ?, now(), now())
                    """, groupId, userId, status);
        }

        assertThatThrownBy(() -> jdbcTemplate.update("""
                INSERT INTO group_members (group_id, user_id, role, status, created_at, updated_at)
                VALUES (?, ?, 'MEMBER', 'PENDING', now(), now())
                """, groupId, userId)).isInstanceOf(DataIntegrityViolationException.class);

        jdbcTemplate.update("DELETE FROM group_members WHERE group_id = ?", groupId);
        assertThatThrownBy(() -> jdbcTemplate.update("""
                INSERT INTO group_members (group_id, user_id, role, status, created_at, updated_at)
                VALUES (?, ?, 'MEMBER', 'INVALID', now(), now())
                """, groupId, userId)).isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbcTemplate.update("""
                INSERT INTO group_members (group_id, user_id, role, status, created_at, updated_at)
                VALUES (?, ?, 'BOSS', 'PENDING', now(), now())
                """, groupId, userId)).isInstanceOf(DataIntegrityViolationException.class);
    }
}
