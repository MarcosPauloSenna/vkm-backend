package com.vkm_backend.unit.group;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.domain.GroupMembers;
import com.vkm_backend.group.infra.mapper.GroupMemberMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.web.dto.MemberStatusResponse;
import com.vkm_backend.group.usecase.LeaveGroupUseCase;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.MemberNotFoundException;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import com.vkm_backend.user.infra.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveGroupUseCaseTest {

    private static final Long GROUP_ID = 10L;
    private static final Long USER_ID = 20L;
    private static final String USERNAME = "player";

    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupMembersRepository groupMembersRepository;
    @Mock
    private GroupMemberMapper mapper;
    @InjectMocks
    private LeaveGroupUseCase useCase;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(USER_ID);
        user.setUsername(USERNAME);

        lenient().when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        lenient().when(groupMembersRepository.save(any(GroupMembersEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(mapper.toDomain(any(GroupMembersEntity.class))).thenReturn(new GroupMembers());
        lenient().when(mapper.toStatusResponse(any(GroupMembers.class)))
                .thenReturn(new MemberStatusResponse(1L, "player", "MEMBER", "LEFT", null));
    }

    @ParameterizedTest
    @EnumSource(value = GroupMemberRole.class, names = {"MEMBER", "ADMIN"})
    void shouldLeaveGroupWhenApprovedMemberOrAdmin(GroupMemberRole role) {
        GroupMembersEntity member = buildMember(role, GroupMemberStatus.APPROVED);
        when(groupMembersRepository.findByGroupId_IdAndUserId_Id(GROUP_ID, USER_ID)).thenReturn(Optional.of(member));

        MemberStatusResponse response = useCase.execute(GROUP_ID, USERNAME);

        assertThat(response).isNotNull();
        assertThat(member.getStatus()).isEqualTo(GroupMemberStatus.LEFT);
        assertThat(member.getApprovedBy()).isSameAs(user);
        assertThat(member.getApprovedAt()).isNotNull();
        verify(groupMembersRepository).save(member);
    }

    @Test
    void shouldCancelRequestWhenMembershipIsPending() {
        GroupMembersEntity member = buildMember(GroupMemberRole.MEMBER, GroupMemberStatus.PENDING);
        when(groupMembersRepository.findByGroupId_IdAndUserId_Id(GROUP_ID, USER_ID)).thenReturn(Optional.of(member));

        useCase.execute(GROUP_ID, USERNAME);

        assertThat(member.getStatus()).isEqualTo(GroupMemberStatus.CANCELLED);
        verify(groupMembersRepository).save(member);
    }

    @Test
    void shouldForbidOwnerToLeaveWithoutTransferringOwnership() {
        GroupMembersEntity member = buildMember(GroupMemberRole.OWNER, GroupMemberStatus.APPROVED);
        when(groupMembersRepository.findByGroupId_IdAndUserId_Id(GROUP_ID, USER_ID)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> useCase.execute(GROUP_ID, USERNAME))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("transferir a titularidade");

        assertThat(member.getStatus()).isEqualTo(GroupMemberStatus.APPROVED);
        verify(groupMembersRepository, never()).save(any(GroupMembersEntity.class));
    }

    @ParameterizedTest
    @CsvSource({
            "SUSPENDED, suspenso",
            "REJECTED, vínculo ativo",
            "CANCELLED, cancelada",
            "LEFT, já saiu"
    })
    void shouldForbidLeavingWhenStatusDoesNotAllow(GroupMemberStatus status, String expectedMessage) {
        GroupMembersEntity member = buildMember(GroupMemberRole.MEMBER, status);
        when(groupMembersRepository.findByGroupId_IdAndUserId_Id(GROUP_ID, USER_ID)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> useCase.execute(GROUP_ID, USERNAME))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(expectedMessage);

        assertThat(member.getStatus()).isEqualTo(status);
        verify(groupMembersRepository, never()).save(any(GroupMembersEntity.class));
    }

    @Test
    void shouldThrowMemberNotFoundWhenUserIsNotLinkedToGroup() {
        when(groupMembersRepository.findByGroupId_IdAndUserId_Id(GROUP_ID, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(GROUP_ID, USERNAME))
                .isInstanceOf(MemberNotFoundException.class);

        verify(groupMembersRepository, never()).save(any(GroupMembersEntity.class));
    }

    private GroupMembersEntity buildMember(GroupMemberRole role, GroupMemberStatus status) {
        GroupMembersEntity member = new GroupMembersEntity();
        member.setId(1L);
        member.setGroupId(new GroupsEntity());
        member.setUserId(user);
        member.setRole(role);
        member.setStatus(status);
        return member;
    }
}
