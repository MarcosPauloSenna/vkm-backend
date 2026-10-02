package com.vkm_backend.unit.group;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.domain.GroupMembers;
import com.vkm_backend.group.infra.mapper.GroupMemberMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.web.dto.MemberRoleRequest;
import com.vkm_backend.group.infra.persistence.web.dto.MemberStatusResponse;
import com.vkm_backend.group.service.GroupMemberAuthorizationService;
import com.vkm_backend.group.usecase.UpdateRoleMemberUseCase;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.MemberNotFoundException;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateRoleMemberUseCaseTest {

    private static final Long GROUP_ID = 1L;
    private static final Long MEMBER_ID = 2L;
    private static final String OWNER_USERNAME = "owner";

    @Mock
    private GroupMemberAuthorizationService authorizationService;
    @Mock
    private GroupMemberMapper mapper;
    @Mock
    private GroupMembersRepository membersRepository;
    @InjectMocks
    private UpdateRoleMemberUseCase useCase;

    private GroupMembersEntity ownerMember;

    @BeforeEach
    void setUp() {
        ownerMember = buildMember(99L, "owner", GroupMemberRole.OWNER, GroupMemberStatus.APPROVED);

        lenient().when(membersRepository.save(any(GroupMembersEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(mapper.toDomain(any(GroupMembersEntity.class))).thenReturn(new GroupMembers());
        lenient().when(mapper.toStatusResponse(any(GroupMembers.class)))
                .thenReturn(new MemberStatusResponse(MEMBER_ID, "member", "ADMIN", "APPROVED", null));
    }

    @Test
    void shouldPromoteApprovedMemberToAdmin() {
        GroupMembersEntity target = buildMember(MEMBER_ID, "member", GroupMemberRole.MEMBER, GroupMemberStatus.APPROVED);
        mockAuthorizedOwnerAndTarget(target);

        MemberStatusResponse response = useCase.execute(GROUP_ID, MEMBER_ID, OWNER_USERNAME,
                new MemberRoleRequest(GroupMemberRole.ADMIN));

        assertThat(response).isNotNull();
        assertThat(target.getRole()).isEqualTo(GroupMemberRole.ADMIN);
        assertThat(target.getApprovedBy()).isSameAs(ownerMember.getUserId());
        assertThat(ownerMember.getRole()).isEqualTo(GroupMemberRole.OWNER);
        verify(membersRepository, times(1)).save(target);
    }

    @Test
    void shouldDemoteAdminToMember() {
        GroupMembersEntity target = buildMember(MEMBER_ID, "member", GroupMemberRole.ADMIN, GroupMemberStatus.APPROVED);
        mockAuthorizedOwnerAndTarget(target);

        useCase.execute(GROUP_ID, MEMBER_ID, OWNER_USERNAME, new MemberRoleRequest(GroupMemberRole.MEMBER));

        assertThat(target.getRole()).isEqualTo(GroupMemberRole.MEMBER);
    }

    @Test
    void shouldTransferOwnershipAndDemoteCurrentOwnerToMember() {
        GroupMembersEntity target = buildMember(MEMBER_ID, "member", GroupMemberRole.ADMIN, GroupMemberStatus.APPROVED);
        mockAuthorizedOwnerAndTarget(target);

        useCase.execute(GROUP_ID, MEMBER_ID, OWNER_USERNAME, new MemberRoleRequest(GroupMemberRole.OWNER));

        assertThat(target.getRole()).isEqualTo(GroupMemberRole.OWNER);
        assertThat(ownerMember.getRole()).isEqualTo(GroupMemberRole.MEMBER);
        verify(membersRepository).save(target);
        verify(membersRepository).save(ownerMember);
    }

    @ParameterizedTest
    @EnumSource(value = GroupMemberStatus.class, names = {"PENDING", "REJECTED", "SUSPENDED", "CANCELLED", "LEFT"})
    void shouldBlockRoleChangeWhenMemberIsNotApproved(GroupMemberStatus status) {
        GroupMembersEntity target = buildMember(MEMBER_ID, "member", GroupMemberRole.MEMBER, status);
        mockAuthorizedOwnerAndTarget(target);

        assertThatThrownBy(() -> useCase.execute(GROUP_ID, MEMBER_ID, OWNER_USERNAME,
                new MemberRoleRequest(GroupMemberRole.ADMIN)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(status.name());

        assertThat(target.getRole()).isEqualTo(GroupMemberRole.MEMBER);
        verify(membersRepository, never()).save(any(GroupMembersEntity.class));
    }

    @Test
    void shouldBlockWhenMemberAlreadyHasRequestedRole() {
        GroupMembersEntity target = buildMember(MEMBER_ID, "member", GroupMemberRole.ADMIN, GroupMemberStatus.APPROVED);
        mockAuthorizedOwnerAndTarget(target);

        assertThatThrownBy(() -> useCase.execute(GROUP_ID, MEMBER_ID, OWNER_USERNAME,
                new MemberRoleRequest(GroupMemberRole.ADMIN)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ADMIN");

        verify(membersRepository, never()).save(any(GroupMembersEntity.class));
    }

    @Test
    void shouldThrowMemberNotFoundWhenMemberDoesNotBelongToGroup() {
        when(authorizationService.authorizeOwner(GROUP_ID, OWNER_USERNAME)).thenReturn(ownerMember);
        when(membersRepository.findByIdAndGroupId_Id(MEMBER_ID, GROUP_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(GROUP_ID, MEMBER_ID, OWNER_USERNAME,
                new MemberRoleRequest(GroupMemberRole.ADMIN)))
                .isInstanceOf(MemberNotFoundException.class);

        verify(membersRepository, never()).save(any(GroupMembersEntity.class));
    }

    @Test
    void shouldPropagateAccessDeniedWhenActorIsNotOwner() {
        when(authorizationService.authorizeOwner(GROUP_ID, OWNER_USERNAME))
                .thenThrow(new AccessDeniedException("Usuário não possui permissão para esta operação."));

        assertThatThrownBy(() -> useCase.execute(GROUP_ID, MEMBER_ID, OWNER_USERNAME,
                new MemberRoleRequest(GroupMemberRole.ADMIN)))
                .isInstanceOf(AccessDeniedException.class);

        verify(membersRepository, never()).save(any(GroupMembersEntity.class));
    }

    private void mockAuthorizedOwnerAndTarget(GroupMembersEntity target) {
        when(authorizationService.authorizeOwner(GROUP_ID, OWNER_USERNAME)).thenReturn(ownerMember);
        when(membersRepository.findByIdAndGroupId_Id(MEMBER_ID, GROUP_ID)).thenReturn(Optional.of(target));
    }

    private GroupMembersEntity buildMember(Long id, String name, GroupMemberRole role, GroupMemberStatus status) {
        GroupMembersEntity member = new GroupMembersEntity();
        member.setId(id);
        member.setGroupId(new GroupsEntity());
        UserEntity user = new UserEntity();
        user.setName(name);
        member.setUserId(user);
        member.setRole(role);
        member.setStatus(status);
        return member;
    }
}
