package com.vkm_backend.integration.group;

import com.vkm_backend.integration.AbstractIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

abstract class AbstractGroupIT extends AbstractIntegrationTest {

    protected static final String PASSWORD = "12345678";
    private static final AtomicInteger SEQ = new AtomicInteger(1000);

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected int next() {
        return SEQ.incrementAndGet();
    }

    protected String newUser(String prefix) throws Exception {
        String username = prefix + next();
        String phone = "7577" + String.format("%07d", next());

        mockMvc.perform(post("/api/v1/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s","birthDate":"01/01/1991","phone":"%s",
                                 "profilePhoto":null,"username":"%s","password":"%s"}
                                """.formatted("Usuario " + username, phone, username, PASSWORD)))
                .andExpect(status().isCreated());

        return username;
    }

    protected String token(String username) throws Exception {
        String body = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("accessToken").asString();
    }

    protected String newUserToken(String prefix) throws Exception {
        return token(newUser(prefix));
    }

    protected Long createGroup(String token, String name, String city) throws Exception {
        String body = mockMvc.perform(post("/api/v1/groups/create")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s","description":"Grupo de teste","city":"%s","state":"Bahia"}
                                """.formatted(name, city)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("group").get("id").asLong();
    }

    protected Long createGroup(String token) throws Exception {
        return createGroup(token, "Grupo " + next(), "Cidade");
    }

    protected ResultActions associate(String token, Long groupId) throws Exception {
        return mockMvc.perform(post("/api/v1/group/{id}/members/associate", groupId)
                .header("Authorization", bearer(token)));
    }

    protected Long associateAndGetMemberId(String token, Long groupId) throws Exception {
        String body = associate(token, groupId)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("member").get("id").asLong();
    }

    protected ResultActions changeStatus(String token, Long groupId, Long memberId, String status) throws Exception {
        return mockMvc.perform(patch("/api/v1/group/{g}/members/{m}/status", groupId, memberId)
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"" + status + "\"}"));
    }

    protected ResultActions changeRole(String token, Long groupId, Long memberId, String role) throws Exception {
        return mockMvc.perform(patch("/api/v1/group/{g}/members/{m}/role", groupId, memberId)
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"role\":\"" + role + "\"}"));
    }

    protected ResultActions leave(String token, Long groupId) throws Exception {
        return mockMvc.perform(delete("/api/v1/group/{g}/members/leave", groupId)
                .header("Authorization", bearer(token)));
    }

    protected Long approvedMember(String approverToken, Long groupId, String memberToken) throws Exception {
        Long memberId = associateAndGetMemberId(memberToken, groupId);
        changeStatus(approverToken, groupId, memberId, "APPROVED").andExpect(status().isOk());
        return memberId;
    }

    protected String memberStatus(Long memberId) {
        return jdbcTemplate.queryForObject("SELECT status FROM group_members WHERE id = ?", String.class, memberId);
    }

    protected String memberRole(Long memberId) {
        return jdbcTemplate.queryForObject("SELECT role FROM group_members WHERE id = ?", String.class, memberId);
    }

    protected String bearer(String token) {
        return "Bearer " + token;
    }
}
