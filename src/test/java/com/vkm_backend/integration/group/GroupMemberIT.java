package com.vkm_backend.integration.group;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GroupMemberIT extends AbstractGroupIT {

    @Test
    void shouldAssociateAsPendingMemberAndRejectDuplicate() throws Exception {
        String ownerToken = newUserToken("mao");
        Long groupId = createGroup(ownerToken);
        String userToken = newUserToken("mau");

        Long memberId = associateAndGetMemberId(userToken, groupId);

        assertThat(memberStatus(memberId)).isEqualTo("PENDING");
        assertThat(memberRole(memberId)).isEqualTo("MEMBER");
        associate(userToken, groupId).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenAssociatingToUnknownGroup() throws Exception {
        String token = newUserToken("maunk");

        associate(token, 99999999L).andExpect(status().isNotFound());
    }

    @Test
    void shouldRequireAuthenticationToAssociate() throws Exception {
        associate("", 1L).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldApproveRejectAndSuspendFollowingTransitionRules() throws Exception {
        String ownerToken = newUserToken("mso");
        Long groupId = createGroup(ownerToken);
        String userToken = newUserToken("msu");
        Long memberId = associateAndGetMemberId(userToken, groupId);

        changeStatus(ownerToken, groupId, memberId, "APPROVED").andExpect(status().isOk())
                .andExpect(jsonPath("$.member.status").value("APPROVED"));
        changeStatus(ownerToken, groupId, memberId, "SUSPENDED").andExpect(status().isOk());
        assertThat(memberStatus(memberId)).isEqualTo("SUSPENDED");
        changeStatus(ownerToken, groupId, memberId, "REJECTED").andExpect(status().isBadRequest());
        changeStatus(ownerToken, groupId, memberId, "APPROVED").andExpect(status().isOk());
        assertThat(memberStatus(memberId)).isEqualTo("APPROVED");
    }

    @Test
    void shouldAllowOnlyOwnerToReapproveSuspendedMember() throws Exception {
        String ownerToken = newUserToken("mro");
        Long groupId = createGroup(ownerToken);
        String adminToken = newUserToken("mra");
        Long adminId = approvedMember(ownerToken, groupId, adminToken);
        changeRole(ownerToken, groupId, adminId, "ADMIN").andExpect(status().isOk());
        String userToken = newUserToken("mru");
        Long memberId = approvedMember(ownerToken, groupId, userToken);

        changeStatus(adminToken, groupId, memberId, "SUSPENDED").andExpect(status().isOk());
        changeStatus(adminToken, groupId, memberId, "APPROVED").andExpect(status().isBadRequest());
        changeStatus(ownerToken, groupId, memberId, "APPROVED").andExpect(status().isOk());
    }

    @Test
    void shouldNotAllowAdminToChangeAnotherAdmin() throws Exception {
        String ownerToken = newUserToken("mpo");
        Long groupId = createGroup(ownerToken);
        String admin1 = newUserToken("mpa");
        Long admin1Id = approvedMember(ownerToken, groupId, admin1);
        changeRole(ownerToken, groupId, admin1Id, "ADMIN").andExpect(status().isOk());
        String admin2 = newUserToken("mpb");
        Long admin2Id = approvedMember(ownerToken, groupId, admin2);
        changeRole(ownerToken, groupId, admin2Id, "ADMIN").andExpect(status().isOk());

        changeStatus(admin1, groupId, admin2Id, "SUSPENDED").andExpect(status().isBadRequest());
        assertThat(memberStatus(admin2Id)).isEqualTo("APPROVED");
    }

    @Test
    void shouldDenyStatusChangeForRegularMember() throws Exception {
        String ownerToken = newUserToken("mdo");
        Long groupId = createGroup(ownerToken);
        String memberToken = newUserToken("mdm");
        approvedMember(ownerToken, groupId, memberToken);
        Long pendingId = associateAndGetMemberId(newUserToken("mdp"), groupId);

        changeStatus(memberToken, groupId, pendingId, "APPROVED").andExpect(status().isForbidden());
    }

    @Test
    void shouldSearchOnlyMembersOfRequestedGroup() throws Exception {
        String ownerToken = newUserToken("mfo");
        Long groupId = createGroup(ownerToken);
        Long otherGroupId = createGroup(newUserToken("mfx"));
        approvedMember(ownerToken, groupId, newUserToken("mf1"));
        approvedMember(ownerToken, groupId, newUserToken("mf2"));

        mockMvc.perform(get("/api/v1/group/{id}/members/search", groupId)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members.totalElements").value(3));

        mockMvc.perform(get("/api/v1/group/{id}/members/search", groupId)
                        .header("Authorization", bearer(ownerToken))
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members.totalElements").value(0));

        mockMvc.perform(get("/api/v1/group/{id}/members/search", otherGroupId)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectMemberSearchWithInvalidSort() throws Exception {
        String ownerToken = newUserToken("mfs");
        Long groupId = createGroup(ownerToken);

        mockMvc.perform(get("/api/v1/group/{id}/members/search", groupId)
                        .header("Authorization", bearer(ownerToken))
                        .param("sort", "password,asc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldPromoteDemoteAndTransferOwnership() throws Exception {
        String ownerToken = newUserToken("mto");
        Long groupId = createGroup(ownerToken);
        Long ownerMemberId = jdbcTemplate.queryForObject(
                "SELECT id FROM group_members WHERE group_id = ? AND role = 'OWNER'", Long.class, groupId);
        String userToken = newUserToken("mtu");
        Long memberId = approvedMember(ownerToken, groupId, userToken);

        changeRole(ownerToken, groupId, memberId, "ADMIN").andExpect(status().isOk())
                .andExpect(jsonPath("$.member.role").value("ADMIN"));
        changeRole(userToken, groupId, memberId, "MEMBER").andExpect(status().isForbidden());
        changeRole(ownerToken, groupId, memberId, "OWNER").andExpect(status().isOk());

        assertThat(memberRole(memberId)).isEqualTo("OWNER");
        assertThat(memberRole(ownerMemberId)).isEqualTo("MEMBER");
    }

    @Test
    void shouldNotChangeRoleOfNonApprovedMember() throws Exception {
        String ownerToken = newUserToken("mnao");
        Long groupId = createGroup(ownerToken);
        Long pendingId = associateAndGetMemberId(newUserToken("mnap"), groupId);

        changeRole(ownerToken, groupId, pendingId, "ADMIN").andExpect(status().isBadRequest());
    }

    @Test
    void shouldLeaveGroupWhenApprovedAndThenReapply() throws Exception {
        String ownerToken = newUserToken("mlo");
        Long groupId = createGroup(ownerToken);
        String userToken = newUserToken("mlu");
        Long memberId = approvedMember(ownerToken, groupId, userToken);

        leave(userToken, groupId).andExpect(status().isOk())
                .andExpect(jsonPath("$.member.status").value("LEFT"));
        assertThat(memberStatus(memberId)).isEqualTo("LEFT");

        associate(userToken, groupId).andExpect(status().isCreated());
        assertThat(memberStatus(memberId)).isEqualTo("PENDING");
    }

    @Test
    void shouldTreatLeaveOfPendingMemberAsCancellation() throws Exception {
        String ownerToken = newUserToken("mco");
        Long groupId = createGroup(ownerToken);
        String userToken = newUserToken("mcu");
        Long memberId = associateAndGetMemberId(userToken, groupId);

        leave(userToken, groupId).andExpect(status().isOk());

        assertThat(memberStatus(memberId)).isEqualTo("CANCELLED");
        leave(userToken, groupId).andExpect(status().isBadRequest());
        associate(userToken, groupId).andExpect(status().isCreated());
    }

    @Test
    void shouldBlockLeaveForOwnerSuspendedAndRejected() throws Exception {
        String ownerToken = newUserToken("mbo");
        Long groupId = createGroup(ownerToken);
        String suspendedToken = newUserToken("mbs");
        Long suspendedId = approvedMember(ownerToken, groupId, suspendedToken);
        changeStatus(ownerToken, groupId, suspendedId, "SUSPENDED").andExpect(status().isOk());
        String rejectedToken = newUserToken("mbr");
        Long rejectedId = associateAndGetMemberId(rejectedToken, groupId);
        changeStatus(ownerToken, groupId, rejectedId, "REJECTED").andExpect(status().isOk());

        leave(ownerToken, groupId).andExpect(status().isBadRequest());
        leave(suspendedToken, groupId).andExpect(status().isBadRequest());
        leave(rejectedToken, groupId).andExpect(status().isBadRequest());

        assertThat(memberStatus(suspendedId)).isEqualTo("SUSPENDED");
        assertThat(memberStatus(rejectedId)).isEqualTo("REJECTED");
    }

    @Test
    void shouldBlockReapplyWithin24HoursAfterRejectionAndAllowAfter() throws Exception {
        String ownerToken = newUserToken("mqo");
        Long groupId = createGroup(ownerToken);
        String userToken = newUserToken("mqu");
        Long memberId = associateAndGetMemberId(userToken, groupId);
        changeStatus(ownerToken, groupId, memberId, "REJECTED").andExpect(status().isOk());

        associate(userToken, groupId).andExpect(status().isBadRequest());

        jdbcTemplate.update("UPDATE group_members SET approved_at = now() - interval '25 hours' WHERE id = ?", memberId);
        associate(userToken, groupId).andExpect(status().isCreated());
        assertThat(memberStatus(memberId)).isEqualTo("PENDING");
    }

    @Test
    void shouldReturnNotFoundWhenLeavingGroupWithoutMembership() throws Exception {
        Long groupId = createGroup(newUserToken("mzo"));

        leave(newUserToken("mzu"), groupId).andExpect(status().isNotFound());
    }
}
