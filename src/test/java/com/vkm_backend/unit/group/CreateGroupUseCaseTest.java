package com.vkm_backend.unit.group;

import com.vkm_backend.group.domain.Groups;
import com.vkm_backend.group.infra.mapper.GroupMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.CreateGroupRequest;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.group.service.MembershipRegistrar;
import com.vkm_backend.group.usecase.CreateGroupUseCase;
import com.vkm_backend.infra.global.exceptions.ConflictException;
import com.vkm_backend.infra.global.exceptions.ResourceNotFoundException;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import com.vkm_backend.user.infra.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


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

    @Mock
    private GroupsRepository groupsRepository;
    @Mock
    private GroupMapper groupMapper;
    @Mock
    private MembershipRegistrar membershipRegistrar;
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
    void shouldCreateGroupAndRegisterCreatorAsOwner() {
        Groups domain = new Groups();
        GroupsEntity entityToSave = new GroupsEntity();
        GroupsEntity savedEntity = new GroupsEntity();
        savedEntity.setId(GROUP_ID);
        GroupResponse expected = new GroupResponse(GROUP_ID, "Volei com Cristo", "Descricao", "Cruz das almas",
                "Bahia", "ACTIVE", USERNAME);

        when(groupsRepository.existsByNameAndCityAndState("Volei com Cristo", "Cruz das almas", "Bahia"))
                .thenReturn(false);
        when(userRepository.findByUsername(USERNAME)).thenReturn(owner);
        when(groupMapper.toDomain(request)).thenReturn(domain);
        when(groupMapper.toEntity(domain)).thenReturn(entityToSave);
        when(groupsRepository.save(entityToSave)).thenReturn(savedEntity);
        when(groupMapper.toDomain(savedEntity)).thenReturn(domain);
        when(groupMapper.toResponse(domain)).thenReturn(expected);

        GroupResponse response = useCase.createGroup(request, USERNAME);

        assertThat(response).isEqualTo(expected);
        assertThat(domain.getOwner()).isSameAs(owner);

        verify(membershipRegistrar).registerOwner(savedEntity, owner);
    }

    @Test
    void shouldThrowConflictWhenGroupNameAlreadyExistsInCity() {
        when(groupsRepository.existsByNameAndCityAndState("Volei com Cristo", "Cruz das almas", "Bahia"))
                .thenReturn(true);

        assertThatThrownBy(() -> useCase.createGroup(request, USERNAME))
                .isInstanceOf(ConflictException.class);

        verify(groupsRepository, never()).save(any(GroupsEntity.class));
        verifyNoInteractions(membershipRegistrar);
    }

    @Test
    void shouldThrowNotFoundWhenAuthenticatedUserDoesNotExist() {
        when(groupsRepository.existsByNameAndCityAndState(any(), any(), any())).thenReturn(false);
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);

        assertThatThrownBy(() -> useCase.createGroup(request, USERNAME))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(groupsRepository, never()).save(any(GroupsEntity.class));
    }
}
