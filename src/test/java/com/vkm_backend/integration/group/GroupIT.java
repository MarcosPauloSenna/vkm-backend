package com.vkm_backend.integration.group;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GroupIT extends AbstractGroupIT {

    @Test
    void shouldCreateGroupAndPersistCreatorAsApprovedOwner() throws Exception {
        String owner = newUser("gowner");
        String token = token(owner);
        String name = "Volei Praia " + next();

        mockMvc.perform(post("/api/v1/groups/create")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s","description":"Desc","city":"Salvador","state":"Bahia"}
                                """.formatted(name)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.group.name").value(name))
                .andExpect(jsonPath("$.group.city").value("Salvador"));

        Long groupId = jdbcTemplate.queryForObject("SELECT id FROM groups WHERE name = ?", Long.class, name);
        String role = jdbcTemplate.queryForObject(
                "SELECT gm.role FROM group_members gm JOIN users u ON u.id = gm.user_id " +
                        "WHERE gm.group_id = ? AND u.username = ?", String.class, groupId, owner);
        String memberStatus = jdbcTemplate.queryForObject(
                "SELECT gm.status FROM group_members gm JOIN users u ON u.id = gm.user_id " +
                        "WHERE gm.group_id = ? AND u.username = ?", String.class, groupId, owner);

        assertThat(role).isEqualTo("OWNER");
        assertThat(memberStatus).isEqualTo("APPROVED");
    }

    @Test
    void shouldRejectGroupWithoutName() throws Exception {
        String token = newUserToken("gnon");

        mockMvc.perform(post("/api/v1/groups/create")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"sem nome\",\"city\":\"X\",\"state\":\"Y\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectDuplicatedGroup() throws Exception {
        String token = newUserToken("gdup");
        String name = "Duplicado " + next();
        createGroup(token, name, "Salvador");

        mockMvc.perform(post("/api/v1/groups/create")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s","description":"Desc","city":"Salvador","state":"Bahia"}
                                """.formatted(name)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldRequireAuthenticationToCreateGroup() throws Exception {
        mockMvc.perform(post("/api/v1/groups/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sem token\",\"city\":\"X\",\"state\":\"Y\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldSearchGroupsByNameAndPaginate() throws Exception {
        String token = newUserToken("gsrc");
        String tag = "Busca" + next();
        createGroup(token, tag + " A", "Ilheus");
        createGroup(token, tag + " B", "Ilheus");
        createGroup(token, tag + " C", "Ilheus");

        mockMvc.perform(get("/api/v1/groups/search")
                        .header("Authorization", bearer(token))
                        .param("name", tag)
                        .param("page", "0").param("size", "2").param("sort", "nome,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groups.totalElements").value(3))
                .andExpect(jsonPath("$.groups.content.length()").value(2))
                .andExpect(jsonPath("$.groups.content[0].name").value(tag + " A"));
    }

    @Test
    void shouldRejectSearchWithInvalidSortField() throws Exception {
        String token = newUserToken("gsort");

        mockMvc.perform(get("/api/v1/groups/search")
                        .header("Authorization", bearer(token))
                        .param("sort", "owner,asc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateGroupWhenOwner() throws Exception {
        String token = newUserToken("gupd");
        Long groupId = createGroup(token);

        mockMvc.perform(patch("/api/v1/groups/{id}", groupId)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Nova descricao\",\"state\":\"Sergipe\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.group.description").value("Nova descricao"))
                .andExpect(jsonPath("$.group.state").value("Sergipe"));
    }

    @Test
    void shouldDenyUpdateForRegularMember() throws Exception {
        String ownerToken = newUserToken("gupo");
        Long groupId = createGroup(ownerToken);
        String memberToken = newUserToken("gupm");
        approvedMember(ownerToken, groupId, memberToken);

        mockMvc.perform(patch("/api/v1/groups/{id}", groupId)
                        .header("Authorization", bearer(memberToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Invasao\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownGroup() throws Exception {
        String token = newUserToken("gupnf");

        mockMvc.perform(patch("/api/v1/groups/{id}", 99999999L)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"x\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldInactivateGroupOnlyWhenOwner() throws Exception {
        String ownerToken = newUserToken("gsto");
        Long groupId = createGroup(ownerToken);
        String adminToken = newUserToken("gsta");
        Long adminMemberId = approvedMember(ownerToken, groupId, adminToken);
        changeRole(ownerToken, groupId, adminMemberId, "ADMIN").andExpect(status().isOk());

        mockMvc.perform(patch("/api/v1/groups/{id}/status", groupId)
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INACTIVE\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/v1/groups/{id}/status", groupId)
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INACTIVE\"}"))
                .andExpect(status().isOk());

        Integer active = jdbcTemplate.queryForObject("SELECT active FROM groups WHERE id = ?", Integer.class, groupId);
        assertThat(active).isZero();
    }
}
