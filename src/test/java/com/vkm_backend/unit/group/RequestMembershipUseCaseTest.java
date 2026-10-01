package com.vkm_backend.unit.group;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.domain.GroupMembers;
import com.vkm_backend.group.infra.mapper.GroupMemberMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.MembershipRequest;
import com.vkm_backend.group.infra.persistence.web.dto.MembershipResponse;
import com.vkm_backend.group.usecase.RequestMembershipUseCase;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.user.infra.persistence.UserEntity;
import com.vkm_backend.user.infra.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestMembershipUseCaseTest {

    private static final Long GROUP_ID = 12L;
    private static final Long USER_ID = 34L;
    private static final String USERNAME = "player";

    @Mock
    private GroupMembersRepository groupMembersRepository;
    @Mock
    private GroupsRepository groupsRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupMemberMapper mapper;
    @InjectMocks
    private RequestMembershipUseCase useCase;

    private UserEntity user;
    private MembershipRequest request;
    private MembershipResponse response;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(USER_ID);
        request = new MembershipRequest(GROUP_ID, USERNAME);
        response = new MembershipResponse(5L, "Volley group", USERNAME, "MEMBER", "PENDING");

        when(groupsRepository.existsById(GROUP_ID)).thenReturn(true);
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
    }

    @Test
    void shouldCreatePendingMembershipWhenUserHasNoExistingMembership() {
        when(groupMembersRepository.findByGroupId_IdAndUserId_Id(GROUP_ID, USER_ID))
                .thenReturn(Optional.empty());
        when(groupsRepository.getReferenceById(GROUP_ID)).thenReturn(new GroupsEntity());
        when(groupMembersRepository.save(any(GroupMembersEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        stubResponseMapping();

        MembershipResponse result = useCase.associate(request);

        assertThat(result).isEqualTo(response);
        verify(groupMembersRepository).save(any(GroupMembersEntity.class));
    }

    @Test
    void shouldRejectReapplicationBefore24HoursHavePassed() {
        GroupMembersEntity rejectedMembership = existingMembership(
                Instant.now().minus(Duration.ofHours(23)));
        when(groupMembersRepository.findByGroupId_IdAndUserId_Id(GROUP_ID, USER_ID))
                .thenReturn(Optional.of(rejectedMembership));

        assertThatThrownBy(() -> useCase.associate(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("após 24 horas");

        verify(groupMembersRepository, never()).save(rejectedMembership);
    }

    @Test
    void shouldReuseRejectedMembershipAfter24Hours() {
        GroupMembersEntity rejectedMembership = existingMembership(
                Instant.now().minus(Duration.ofHours(25)));
        when(groupMembersRepository.findByGroupId_IdAndUserId_Id(GROUP_ID, USER_ID))
                .thenReturn(Optional.of(rejectedMembership));
        when(groupMembersRepository.save(rejectedMembership)).thenReturn(rejectedMembership);
        stubResponseMapping();

        MembershipResponse result = useCase.associate(request);

        assertThat(result).isEqualTo(response);
        assertThat(rejectedMembership.getStatus()).isEqualTo(GroupMemberStatus.PENDING);
        assertThat(rejectedMembership.getRole()).isEqualTo(GroupMemberRole.MEMBER);
        assertThat(rejectedMembership.getApprovedAt()).isNull();
        assertThat(rejectedMembership.getApprovedBy()).isNull();
        verify(groupMembersRepository).save(rejectedMembership);
    }

    @Test
    void shouldNotAllowAnotherRequestWhileMembershipIsNotRejected() {
        GroupMembersEntity pendingMembership = existingMembership(Instant.now());
        pendingMembership.setStatus(GroupMemberStatus.PENDING);
        when(groupMembersRepository.findByGroupId_IdAndUserId_Id(GROUP_ID, USER_ID))
                .thenReturn(Optional.of(pendingMembership));

        assertThatThrownBy(() -> useCase.associate(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ja associado");

        verify(groupMembersRepository, never()).save(any(GroupMembersEntity.class));
    }

    private GroupMembersEntity existingMembership(Instant rejectedAt) {
        GroupMembersEntity membership = new GroupMembersEntity();
        membership.setId(8L);
        membership.setGroupId(new GroupsEntity());
        membership.setUserId(user);
        membership.setRole(GroupMemberRole.ADMIN);
        membership.setStatus(GroupMemberStatus.REJECTED);
        membership.setApprovedAt(rejectedAt);
        membership.setApprovedBy(new UserEntity());
        return membership;
    }

    private void stubResponseMapping() {
        when(mapper.toDomain(any(GroupMembersEntity.class))).thenReturn(new GroupMembers());
        when(mapper.toResponse(any(GroupMembers.class))).thenReturn(response);
    }
}
