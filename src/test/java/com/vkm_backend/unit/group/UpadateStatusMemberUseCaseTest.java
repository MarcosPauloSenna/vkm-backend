package com.vkm_backend.unit.group;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.domain.GroupMembers;
import com.vkm_backend.group.infra.mapper.GroupMemberMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.web.dto.MemberStatusRequest;
import com.vkm_backend.group.infra.persistence.web.dto.MemberStatusResponse;
import com.vkm_backend.group.service.GroupMemberAuthorizationService;
import com.vkm_backend.group.usecase.UpadateStatusMemberUseCase;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.user.infra.persistence.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpadateStatusMemberUseCaseTest {

    private static final Long GROUP_ID = 1L;
    private static final Long MEMBER_ID = 2L;
    private static final String ACTING_USERNAME = "manager";

    @Mock
    private GroupMemberAuthorizationService authorizationService;
    @Mock
    private GroupMembersRepository membersRepository;
    @Mock
    private GroupMemberMapper mapper;
    @InjectMocks
    private UpadateStatusMemberUseCase useCase;

    @BeforeEach
    void setUp() {
        lenient().when(membersRepository.save(any(GroupMembersEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(mapper.toDomain(any(GroupMembersEntity.class))).thenReturn(new GroupMembers());
        lenient().when(mapper.toStatusResponse(any(GroupMembers.class)))
                .thenReturn(new MemberStatusResponse(MEMBER_ID, "member", "MEMBER", "APPROVED", null));
    }

    // ---- Transições permitidas tanto para ADMIN quanto para OWNER ----

    @ParameterizedTest
    @EnumSource(value = GroupMemberRole.class, names = {"ADMIN", "OWNER"})
    void shouldAllowPendingToApprovedForAdminAndOwner(GroupMemberRole actingRole) {
        assertTransitionAllowed(actingRole, GroupMemberStatus.PENDING, GroupMemberStatus.APPROVED);
    }

    @ParameterizedTest
    @EnumSource(value = GroupMemberRole.class, names = {"ADMIN", "OWNER"})
    void shouldAllowPendingToRejectedForAdminAndOwner(GroupMemberRole actingRole) {
        assertTransitionAllowed(actingRole, GroupMemberStatus.PENDING, GroupMemberStatus.REJECTED);
    }

    @ParameterizedTest
    @EnumSource(value = GroupMemberRole.class, names = {"ADMIN", "OWNER"})
    void shouldAllowApprovedToSuspendedForAdminAndOwner(GroupMemberRole actingRole) {
        assertTransitionAllowed(actingRole, GroupMemberStatus.APPROVED, GroupMemberStatus.SUSPENDED);
    }

    // ---- Transições exclusivas do OWNER ----

    @Test
    void shouldAllowRejectedToPendingOnlyForOwner() {
        assertTransitionAllowed(GroupMemberRole.OWNER, GroupMemberStatus.REJECTED, GroupMemberStatus.PENDING);
        assertTransitionForbiddenForRole(GroupMemberRole.ADMIN, GroupMemberStatus.REJECTED, GroupMemberStatus.PENDING,
                "proprietário");
    }

    @Test
    void shouldAllowRejectedToApprovedOnlyForOwner() {
        assertTransitionAllowed(GroupMemberRole.OWNER, GroupMemberStatus.REJECTED, GroupMemberStatus.APPROVED);
        assertTransitionForbiddenForRole(GroupMemberRole.ADMIN, GroupMemberStatus.REJECTED, GroupMemberStatus.APPROVED,
                "proprietário");
    }

    @Test
    void shouldAllowSuspendedToPendingOnlyForOwner() {
        assertTransitionAllowed(GroupMemberRole.OWNER, GroupMemberStatus.SUSPENDED, GroupMemberStatus.PENDING);
        assertTransitionForbiddenForRole(GroupMemberRole.ADMIN, GroupMemberStatus.SUSPENDED, GroupMemberStatus.PENDING,
                "proprietário");
    }

    @Test
    void shouldAllowSuspendedToApprovedOnlyForOwner() {
        assertTransitionAllowed(GroupMemberRole.OWNER, GroupMemberStatus.SUSPENDED, GroupMemberStatus.APPROVED);
        assertTransitionForbiddenForRole(GroupMemberRole.ADMIN, GroupMemberStatus.SUSPENDED, GroupMemberStatus.APPROVED,
                "proprietário");
    }

    // ---- Proibição universal SUSPENDED <-> REJECTED, mesmo para o OWNER ----

    @ParameterizedTest
    @EnumSource(value = GroupMemberRole.class, names = {"ADMIN", "OWNER"})
    void shouldForbidSuspendedToRejectedForAnyRole(GroupMemberRole actingRole) {
        assertTransitionForbiddenForRole(actingRole, GroupMemberStatus.SUSPENDED, GroupMemberStatus.REJECTED,
                "não permitida");
    }

    @ParameterizedTest
    @EnumSource(value = GroupMemberRole.class, names = {"ADMIN", "OWNER"})
    void shouldForbidRejectedToSuspendedForAnyRole(GroupMemberRole actingRole) {
        assertTransitionForbiddenForRole(actingRole, GroupMemberStatus.REJECTED, GroupMemberStatus.SUSPENDED,
                "não permitida");
    }

    // ---- Transições não mapeadas também devem ser bloqueadas ----

    @ParameterizedTest
    @EnumSource(value = GroupMemberRole.class, names = {"ADMIN", "OWNER"})
    void shouldForbidApprovedToPendingForAnyRole(GroupMemberRole actingRole) {
        assertTransitionForbiddenForRole(actingRole, GroupMemberStatus.APPROVED, GroupMemberStatus.PENDING,
                "não permitida");
    }

    private void assertTransitionAllowed(GroupMemberRole actingRole, GroupMemberStatus from, GroupMemberStatus to) {
        GroupMembersEntity actingMember = buildMember(GroupMemberRole.MEMBER, GroupMemberStatus.APPROVED);
        actingMember.setRole(actingRole);
        GroupMembersEntity targetMember = buildMember(GroupMemberRole.MEMBER, from);

        when(authorizationService.authorizeAdminAndOwner(GROUP_ID, ACTING_USERNAME)).thenReturn(actingMember);
        when(membersRepository.findByIdAndGroupId_Id(MEMBER_ID, GROUP_ID)).thenReturn(Optional.of(targetMember));

        MemberStatusResponse response = useCase.updateStatusMember(GROUP_ID, MEMBER_ID, ACTING_USERNAME,
                new MemberStatusRequest(to));

        assertThat(response).isNotNull();
        assertThat(targetMember.getStatus()).isEqualTo(to);
    }

    private void assertTransitionForbiddenForRole(GroupMemberRole actingRole, GroupMemberStatus from, GroupMemberStatus to,
                                                   String expectedMessageFragment) {
        GroupMembersEntity actingMember = buildMember(GroupMemberRole.MEMBER, GroupMemberStatus.APPROVED);
        actingMember.setRole(actingRole);
        GroupMembersEntity targetMember = buildMember(GroupMemberRole.MEMBER, from);

        when(authorizationService.authorizeAdminAndOwner(GROUP_ID, ACTING_USERNAME)).thenReturn(actingMember);
        when(membersRepository.findByIdAndGroupId_Id(MEMBER_ID, GROUP_ID)).thenReturn(Optional.of(targetMember));

        assertThatThrownBy(() -> useCase.updateStatusMember(GROUP_ID, MEMBER_ID, ACTING_USERNAME,
                new MemberStatusRequest(to)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(expectedMessageFragment);
    }

    private GroupMembersEntity buildMember(GroupMemberRole role, GroupMemberStatus status) {
        GroupMembersEntity member = new GroupMembersEntity();
        member.setId(MEMBER_ID);
        member.setGroupId(new GroupsEntity());
        UserEntity user = new UserEntity();
        user.setName("member");
        member.setUserId(user);
        member.setRole(role);
        member.setStatus(status);
        return member;
    }
}
