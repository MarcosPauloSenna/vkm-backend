package com.vkm_backend.integration.group;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MyGroupsIT extends AbstractGroupIT {

    private static final String URL = "/api/v1/user/mygroups";

    @Test
    void shouldListOnlyGroupsOfAuthenticatedUser() throws Exception {
        String token = newUserToken("mgu");
        String tag = "Meus" + next();
        createGroup(token, tag + " A", "Salvador");
        createGroup(token, tag + " B", "Salvador");
        createGroup(newUserToken("mgx"), tag + " Alheio", "Salvador");

        mockMvc.perform(get(URL).header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groups.totalElements").value(2));
    }

    @Test
    void shouldIncludeGroupsJoinedAsMember() throws Exception {
        String ownerToken = newUserToken("mgjo");
        Long groupId = createGroup(ownerToken);
        String userToken = newUserToken("mgju");
        approvedMember(ownerToken, groupId, userToken);

        mockMvc.perform(get(URL).header("Authorization", bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groups.totalElements").value(1))
                .andExpect(jsonPath("$.groups.content[0].id").value(groupId));
    }

    @Test
    void shouldFilterByOwnGroupId() throws Exception {
        String token = newUserToken("mgf");
        createGroup(token);
        Long target = createGroup(token);

        mockMvc.perform(get(URL).header("Authorization", bearer(token)).param("id", target.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groups.totalElements").value(1))
                .andExpect(jsonPath("$.groups.content[0].id").value(target));
    }

    @Test
    void shouldNotExposeGroupOfAnotherUserWhenFilteringById() throws Exception {
        String token = newUserToken("mgs");
        createGroup(token);
        Long foreignGroup = createGroup(newUserToken("mgsx"));

        mockMvc.perform(get(URL).header("Authorization", bearer(token)).param("id", foreignGroup.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groups.totalElements").value(0));
    }

    @Test
    void shouldPaginateAndSort() throws Exception {
        String token = newUserToken("mgp");
        String tag = "Pag" + next();
        createGroup(token, tag + " A", "Salvador");
        createGroup(token, tag + " B", "Salvador");
        createGroup(token, tag + " C", "Salvador");

        mockMvc.perform(get(URL).header("Authorization", bearer(token))
                        .param("page", "0").param("size", "2").param("sort", "nome,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groups.totalElements").value(3))
                .andExpect(jsonPath("$.groups.content.length()").value(2))
                .andExpect(jsonPath("$.groups.content[0].name").value(tag + " C"));
    }

    @Test
    void shouldRejectInvalidSortField() throws Exception {
        String token = newUserToken("mgi");
        createGroup(token);

        mockMvc.perform(get(URL).header("Authorization", bearer(token)).param("sort", "owner,asc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnEmptyPageForUserWithoutGroups() throws Exception {
        String token = newUserToken("mgn");

        mockMvc.perform(get(URL).header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groups.totalElements").value(0));
    }

    @Test
    void shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get(URL)).andExpect(status().isUnauthorized());
    }
}
