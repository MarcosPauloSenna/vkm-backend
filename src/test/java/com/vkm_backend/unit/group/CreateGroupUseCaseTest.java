package com.vkm_backend.unit.group;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.domain.Groups;
import com.vkm_backend.group.infra.mapper.GroupMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.CreateGroupRequest;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.group.infra.persistence.web.dto.MembershipRequest;
import com.vkm_backend.group.infra.persistence.web.dto.MembershipResponse;
import com.vkm_backend.group.usecase.CreateGroupUseCase;
import com.vkm_backend.group.usecase.RequestMembershipUseCase;
import com.vkm_backend.infra.global.exceptions.ConflictException;
import com.vkm_backend.infra.global.exceptions.ResourceNotFoundException;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import com.vkm_backend.user.infra.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateGroupUseCaseTest {

    private static final String USERNAME = "owner";
    private static final Long GROUP_ID = 5L;
    private static final Long MEMBER_ID = 9L;

    @Mock
    private GroupsRepository groupsRepository;
    @Mock
    private GroupMapper groupMapper;
    @Mock
    private GroupMembersRepository groupMembersRepository;
    @Mock
    private RequestMembershipUseCase requestMembershipUseCase;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CreateGroupUseCase useCase;

    private CreateGroupRequest request;
    private UserEntity owner;

    @BeforeEach
    void setUp() {
        request = new CreateGroupRequest("Volei com Cristo", "Descricao", "Cruz das almas", "Bahia");
        owner = new UserEntity();
        owner.setId(1L);
        owner.setUsername(USERNAME);
    }

    @Test
    void shouldCreateGroupAndPromoteCreatorToApprovedOwner() {
        Groups domain = new Groups();
        GroupsEntity entityToSave = new GroupsEntity();
        GroupsEntity savedEntity = new GroupsEntity();
        savedEntity.setId(GROUP_ID);
        GroupMembersEntity pendingMember = new GroupMembersEntity();
        pendingMember.setId(MEMBER_ID);
        pendingMember.setRole(GroupMemberRole.MEMBER);
        pendingMember.setStatus(GroupMemberStatus.PENDING);
        GroupResponse expected = new GroupResponse(GROUP_ID, "Volei com Cristo", "Descricao", "Cruz das almas",
                "Bahia", "ACTIVE", USERNAME);

        when(groupsRepository.existsByNameAndCityAndState("Volei com Cristo", "Cruz das almas", "Bahia"))
                .thenReturn(false);
        when(userRepository.findByUsername(USERNAME)).thenReturn(owner);
        when(groupMapper.toDomain(request)).thenReturn(domain);
        when(groupMapper.toEntity(domain)).thenReturn(entityToSave);
        when(groupsRepository.save(entityToSave)).thenReturn(savedEntity);
        when(groupMapper.toDomain(savedEntity)).thenReturn(domain);
        when(requestMembershipUseCase.associate(new MembershipRequest(GROUP_ID, USERNAME)))
                .thenReturn(new MembershipResponse(MEMBER_ID, "Volei com Cristo", USERNAME, "MEMBER", "PENDING"));
        when(groupMembersRepository.findById(MEMBER_ID)).thenReturn(Optional.of(pendingMember));
        when(groupMapper.toResponse(domain)).thenReturn(expected);

        GroupResponse response = useCase.createGroup(request, USERNAME);

        assertThat(response).isEqualTo(expected);
        assertThat(domain.getOwner()).isSameAs(owner);

        ArgumentCaptor<GroupMembersEntity> captor = ArgumentCaptor.forClass(GroupMembersEntity.class);
        verify(groupMembersRepository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo(GroupMemberRole.OWNER);
        assertThat(captor.getValue().getStatus()).isEqualTo(GroupMemberStatus.APPROVED);
        assertThat(captor.getValue().getApprovedAt()).isNotNull();
    }

    @Test
    void shouldThrowConflictWhenGroupNameAlreadyExistsInCity() {
        when(groupsRepository.existsByNameAndCityAndState("Volei com Cristo", "Cruz das almas", "Bahia"))
                .thenReturn(true);

        assertThatThrownBy(() -> useCase.createGroup(request, USERNAME))
                .isInstanceOf(ConflictException.class);

        verify(groupsRepository, never()).save(any(GroupsEntity.class));
        verifyNoInteractions(requestMembershipUseCase);
    }

    @Test
    void shouldThrowNotFoundWhenAuthenticatedUserDoesNotExist() {
        when(groupsRepository.existsByNameAndCityAndState(any(), any(), any())).thenReturn(false);
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);

        assertThatThrownBy(() -> useCase.createGroup(request, USERNAME))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(groupsRepository, never()).save(any(GroupsEntity.class));
    }

    @Test
    void shouldFailWhenCreatedMembershipCannotBeLoaded() {
        Groups domain = new Groups();
        GroupsEntity savedEntity = new GroupsEntity();
        savedEntity.setId(GROUP_ID);

        when(groupsRepository.existsByNameAndCityAndState(any(), any(), any())).thenReturn(false);
        when(userRepository.findByUsername(USERNAME)).thenReturn(owner);
        when(groupMapper.toDomain(request)).thenReturn(domain);
        when(groupMapper.toEntity(domain)).thenReturn(new GroupsEntity());
        when(groupsRepository.save(any(GroupsEntity.class))).thenReturn(savedEntity);
        when(groupMapper.toDomain(savedEntity)).thenReturn(domain);
        when(requestMembershipUseCase.associate(any(MembershipRequest.class)))
                .thenReturn(new MembershipResponse(MEMBER_ID, "g", USERNAME, "MEMBER", "PENDING"));
        when(groupMembersRepository.findById(MEMBER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.createGroup(request, USERNAME))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(groupMembersRepository, never()).save(any(GroupMembersEntity.class));
    }
}
