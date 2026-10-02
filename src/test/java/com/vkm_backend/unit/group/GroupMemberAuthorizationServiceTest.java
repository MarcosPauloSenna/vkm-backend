package com.vkm_backend.unit.group;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.infra.mapper.GroupMemberMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.service.GroupMemberAuthorizationService;
import com.vkm_backend.infra.global.exceptions.GroupNotFoundException;
import com.vkm_backend.infra.global.exceptions.MemberNotFoundException;
import com.vkm_backend.infra.global.exceptions.ValidationException;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import com.vkm_backend.user.infra.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupMemberAuthorizationServiceTest {

    private static final Long GROUP_ID = 1L;
    private static final String USERNAME = "player";

    @Mock
    private GroupMembersRepository membersRepository;
    @Mock
    private GroupMemberMapper mapper;
    @Mock
    private GroupsRepository groupsRepository;
    @Mock
    private UserRepository userRepository;

    private GroupMemberAuthorizationService service;
    private GroupsEntity group;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        service = new GroupMemberAuthorizationService(membersRepository, mapper, groupsRepository, userRepository);
        group = new GroupsEntity();
        group.setId(GROUP_ID);
        user = new UserEntity();
        user.setUsername(USERNAME);
    }

    @ParameterizedTest
    @EnumSource(value = GroupMemberRole.class)
    void shouldAuthorizeAnyApprovedMember(GroupMemberRole role) {
        GroupMembersEntity member = givenMembership(role, GroupMemberStatus.APPROVED);

        assertThat(service.authorize(GROUP_ID, USERNAME)).isSameAs(member);
    }

    @ParameterizedTest
    @EnumSource(value = GroupMemberRole.class, names = {"ADMIN", "OWNER"})
    void shouldAuthorizeAdminAndOwnerForAdminOperations(GroupMemberRole role) {
        GroupMembersEntity member = givenMembership(role, GroupMemberStatus.APPROVED);

        assertThat(service.authorizeAdminAndOwner(GROUP_ID, USERNAME)).isSameAs(member);
    }

    @Test
    void shouldDenyMemberForAdminOperations() {
        givenMembership(GroupMemberRole.MEMBER, GroupMemberStatus.APPROVED);

        assertThatThrownBy(() -> service.authorizeAdminAndOwner(GROUP_ID, USERNAME))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void shouldAuthorizeOnlyOwnerForOwnerOperations() {
        GroupMembersEntity member = givenMembership(GroupMemberRole.OWNER, GroupMemberStatus.APPROVED);

        assertThat(service.authorizeOwner(GROUP_ID, USERNAME)).isSameAs(member);
    }

    @ParameterizedTest
    @EnumSource(value = GroupMemberRole.class, names = {"ADMIN", "MEMBER"})
    void shouldDenyNonOwnerForOwnerOperations(GroupMemberRole role) {
        givenMembership(role, GroupMemberStatus.APPROVED);

        assertThatThrownBy(() -> service.authorizeOwner(GROUP_ID, USERNAME))
                .isInstanceOf(AccessDeniedException.class);
    }

    @ParameterizedTest
    @EnumSource(value = GroupMemberStatus.class, names = {"PENDING", "REJECTED", "SUSPENDED", "CANCELLED", "LEFT"})
    void shouldDenyMembershipThatIsNotApproved(GroupMemberStatus status) {
        givenMembership(GroupMemberRole.OWNER, status);

        assertThatThrownBy(() -> service.authorize(GROUP_ID, USERNAME))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(status.name());
        assertThatThrownBy(() -> service.authorizeAdminAndOwner(GROUP_ID, USERNAME))
                .isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> service.authorizeOwner(GROUP_ID, USERNAME))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void shouldThrowMemberNotFoundWhenUserHasNoMembershipInGroup() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(groupsRepository.findById(GROUP_ID)).thenReturn(Optional.of(group));
        when(membersRepository.findByGroupIdAndUserId(group, user)).thenReturn(null);

        assertThatThrownBy(() -> service.authorize(GROUP_ID, USERNAME))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void shouldThrowGroupNotFoundWhenGroupDoesNotExist() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(groupsRepository.findById(GROUP_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.authorize(GROUP_ID, USERNAME))
                .isInstanceOf(GroupNotFoundException.class);
    }

    private GroupMembersEntity givenMembership(GroupMemberRole role, GroupMemberStatus status) {
        GroupMembersEntity member = new GroupMembersEntity();
        member.setId(5L);
        member.setGroupId(group);
        member.setUserId(user);
        member.setRole(role);
        member.setStatus(status);

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(groupsRepository.findById(GROUP_ID)).thenReturn(Optional.of(group));
        when(membersRepository.findByGroupIdAndUserId(group, user)).thenReturn(member);
        return member;
    }
}
