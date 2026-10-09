package com.vkm_backend.integration.group;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TeamsIT extends AbstractGroupIT {

    @Test
    void shouldCreateAndReuseTeamForSameCompositionRegardlessOfMemberOrder() throws Exception {
        String ownerToken = newUserToken("towner");
        Long groupId = createGroup(ownerToken);
        Long ownerMemberId = ownerMemberId(groupId);
        Long secondMemberId = approvedMember(ownerToken, groupId, newUserToken("tplay"));

        String firstResponse = createTeam(ownerToken, groupId, "Equipe Azul", ownerMemberId, secondMemberId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.team.name").value("Equipe Azul"))
                .andExpect(jsonPath("$.team.teamSize").value(2))
                .andReturn().getResponse().getContentAsString();
        long teamId = objectMapper.readTree(firstResponse).get("team").get("id").asLong();

        String reusedResponse = createTeam(ownerToken, groupId, "Nome ignorado", secondMemberId, ownerMemberId)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(objectMapper.readTree(reusedResponse).get("team").get("id").asLong()).isEqualTo(teamId);
        assertThat(objectMapper.readTree(reusedResponse).get("team").get("name").asString()).isEqualTo("Equipe Azul");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM teams WHERE group_id = ?", Integer.class, groupId)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM team_members WHERE teams_id = ?", Integer.class, teamId)).isEqualTo(2);
    }

    @Test
    void shouldRejectCompositionContainingMemberThatIsNotApproved() throws Exception {
        String ownerToken = newUserToken("tapp");
        Long groupId = createGroup(ownerToken);
        Long ownerMemberId = ownerMemberId(groupId);
        String pendingToken = newUserToken("tpend");
        Long pendingMemberId = associateAndGetMemberId(pendingToken, groupId);

        createTeam(ownerToken, groupId, "Pending team", ownerMemberId, pendingMemberId)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldGenerateTeamNameWhenNameIsOmitted() throws Exception {
        String ownerToken = newUserToken("tname");
        Long groupId = createGroup(ownerToken);
        Long ownerMemberId = ownerMemberId(groupId);

        String body = mockMvc.perform(post("/api/v1/groups/{groupId}/teams/create", groupId)
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"memberIds":[%d]}
                                """.formatted(ownerMemberId)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(objectMapper.readTree(body).get("team").get("name").asString()).isNotBlank();
    }

    @Test
    void shouldSearchGroupTeamsWithPagination() throws Exception {
        String ownerToken = newUserToken("tsrch");
        Long groupId = createGroup(ownerToken);
        Long ownerMemberId = ownerMemberId(groupId);
        Long firstMember = approvedMember(ownerToken, groupId, newUserToken("tfirst"));
        Long secondMember = approvedMember(ownerToken, groupId, newUserToken("tsec"));
        createTeam(ownerToken, groupId, "Search A", ownerMemberId, firstMember).andExpect(status().isOk());
        createTeam(ownerToken, groupId, "Search B", ownerMemberId, secondMember).andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/groups/{groupId}/teams/search", groupId)
                        .header("Authorization", bearer(ownerToken))
                        .param("page", "0")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.size").value(1));
    }

    @Test
    void shouldReturnTeamCompositionAndAllowMemberToRenameTeam() throws Exception {
        String ownerToken = newUserToken("tdetl");
        Long groupId = createGroup(ownerToken);
        Long ownerMemberId = ownerMemberId(groupId);
        Long memberId = approvedMember(ownerToken, groupId, newUserToken("trenm"));
        String body = createTeam(ownerToken, groupId, "Original", ownerMemberId, memberId)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long teamId = objectMapper.readTree(body).get("team").get("id").asLong();

        mockMvc.perform(get("/api/v1/groups/{groupId}/teams/{teamId}", groupId, teamId)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.team.id").value(teamId))
                .andExpect(jsonPath("$.team.members.length()").value(2));

        mockMvc.perform(patch("/api/v1/groups/{groupId}/teams/{teamId}/name", groupId, teamId)
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newName\":\"Renamed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.team.name").value("Renamed"));
    }

    @Test
    void shouldNotExposeTeamFromAnotherGroup() throws Exception {
        String firstOwnerToken = newUserToken("tgrpa");
        Long firstGroupId = createGroup(firstOwnerToken);
        Long firstMemberId = ownerMemberId(firstGroupId);
        String firstResponse = createTeam(firstOwnerToken, firstGroupId, "Private team", firstMemberId)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long teamId = objectMapper.readTree(firstResponse).get("team").get("id").asLong();

        String otherOwnerToken = newUserToken("tgrpb");
        Long otherGroupId = createGroup(otherOwnerToken);

        mockMvc.perform(get("/api/v1/groups/{groupId}/teams/{teamId}", otherGroupId, teamId)
                        .header("Authorization", bearer(otherOwnerToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldEnforceDatabaseUniquenessForCompositionAndTeamMembers() throws Exception {
        String ownerToken = newUserToken("tuniq");
        Long groupId = createGroup(ownerToken);
        Long memberId = ownerMemberId(groupId);
        String body = createTeam(ownerToken, groupId, "Unique", memberId)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long teamId = objectMapper.readTree(body).get("team").get("id").asLong();
        String compositionHash = jdbcTemplate.queryForObject(
                "SELECT composition_hash FROM teams WHERE id = ?", String.class, teamId);

        assertThatThrownBy(() -> jdbcTemplate.update(
                "INSERT INTO teams (group_id, name, team_size, composition_hash, created_at) " +
                        "VALUES (?, ?, ?, ?, ?)",
                groupId, "Duplicate composition", 1, compositionHash, Timestamp.from(java.time.Instant.now())))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> jdbcTemplate.update(
                "INSERT INTO team_members (teams_id, group_members_id, created_at) VALUES (?, ?, ?)",
                teamId, memberId, Timestamp.from(java.time.Instant.now())))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private org.springframework.test.web.servlet.ResultActions createTeam(
            String token, Long groupId, String name, Long... memberIds) throws Exception {
        String ids = java.util.Arrays.stream(memberIds)
                .map(String::valueOf)
                .collect(java.util.stream.Collectors.joining(","));
        return mockMvc.perform(post("/api/v1/groups/{groupId}/teams/create", groupId)
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name":"%s","memberIds":[%s]}
                        """.formatted(name, ids)));
    }

    private Long ownerMemberId(Long groupId) {
        return jdbcTemplate.queryForObject(
                "SELECT gm.id FROM group_members gm JOIN groups g ON g.id = gm.group_id " +
                        "WHERE gm.group_id = ? AND gm.user_id = g.owner_id AND gm.status = 'APPROVED'",
                Long.class, groupId);
    }
}
