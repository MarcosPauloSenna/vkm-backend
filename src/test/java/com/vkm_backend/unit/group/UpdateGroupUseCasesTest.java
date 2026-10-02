package com.vkm_backend.unit.group;

import com.vkm_backend.group.domain.GroupActive;
import com.vkm_backend.group.domain.Groups;
import com.vkm_backend.group.infra.mapper.GroupMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.group.infra.persistence.web.dto.UpdateGroupRequest;
import com.vkm_backend.group.infra.persistence.web.dto.UpdateGroupStatusRequest;
import com.vkm_backend.group.service.GroupAccessPolicy;
import com.vkm_backend.group.usecase.ActiveInativeGroupUseCase;
import com.vkm_backend.group.usecase.UpdateGroupUseCase;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.GroupNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateGroupUseCasesTest {

    private static final Long GROUP_ID = 3L;
    private static final String USERNAME = "manager";

    @Mock
    private GroupsRepository groupsRepository;
    @Mock
    private GroupMapper groupMapper;
    @Mock
    private GroupAccessPolicy authorizationService;
    @InjectMocks
    private UpdateGroupUseCase updateUseCase;
    @InjectMocks
    private ActiveInativeGroupUseCase statusUseCase;

    private GroupsEntity group;
    private final GroupResponse response = new GroupResponse(GROUP_ID, "n", "d", "c", "s", "ACTIVE", "owner");

    @BeforeEach
    void setUp() {
        group = new GroupsEntity();
        group.setId(GROUP_ID);
        group.setName("Original");
        group.setDescription("Desc original");
        group.setCity("Cidade");
        group.setState("Estado");
        group.setActive(GroupActive.ACTIVE);

        lenient().when(groupsRepository.save(any(GroupsEntity.class))).thenAnswer(i -> i.getArgument(0));
        lenient().when(groupMapper.toDomain(any(GroupsEntity.class))).thenReturn(new Groups());
        lenient().when(groupMapper.toResponse(any(Groups.class))).thenReturn(response);
    }

    @Nested
    class UpdateGroup {
        @Test
        void shouldUpdateOnlyProvidedFields() {
            when(groupsRepository.findById(GROUP_ID)).thenReturn(Optional.of(group));

            GroupResponse result = updateUseCase.updateGroup(
                    new UpdateGroupRequest("Novo nome", null, null, "Bahia"), GROUP_ID, USERNAME);

            assertThat(result).isEqualTo(response);
            assertThat(group.getName()).isEqualTo("Novo nome");
            assertThat(group.getState()).isEqualTo("Bahia");
            assertThat(group.getDescription()).isEqualTo("Desc original");
            assertThat(group.getCity()).isEqualTo("Cidade");
            verify(authorizationService).authorizeAdminAndOwner(GROUP_ID, USERNAME);
            verify(groupsRepository).save(group);
        }

        @Test
        void shouldThrowGroupNotFoundWhenGroupDoesNotExist() {
            when(groupsRepository.findById(GROUP_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> updateUseCase.updateGroup(
                    new UpdateGroupRequest("Novo", null, null, null), GROUP_ID, USERNAME))
                    .isInstanceOf(GroupNotFoundException.class);

            verify(groupsRepository, never()).save(any(GroupsEntity.class));
        }

        @Test
        void shouldRejectRequestWithoutAnyField() {
            when(groupsRepository.findById(GROUP_ID)).thenReturn(Optional.of(group));

            assertThatThrownBy(() -> updateUseCase.updateGroup(
                    new UpdateGroupRequest(null, null, null, null), GROUP_ID, USERNAME))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Nenhum dado informado");

            verify(groupsRepository, never()).save(any(GroupsEntity.class));
        }

        @Test
        void shouldNotUpdateWhenActorIsNotAdminOrOwner() {
            when(groupsRepository.findById(GROUP_ID)).thenReturn(Optional.of(group));
            when(authorizationService.authorizeAdminAndOwner(GROUP_ID, USERNAME))
                    .thenThrow(new AccessDeniedException("negado"));

            assertThatThrownBy(() -> updateUseCase.updateGroup(
                    new UpdateGroupRequest("Novo", null, null, null), GROUP_ID, USERNAME))
                    .isInstanceOf(AccessDeniedException.class);

            assertThat(group.getName()).isEqualTo("Original");
            verify(groupsRepository, never()).save(any(GroupsEntity.class));
        }
    }

    @Nested
    class ActiveInactiveGroup {
        @Test
        void shouldInactivateGroupWhenOwner() {
            when(groupsRepository.findById(GROUP_ID)).thenReturn(Optional.of(group));

            GroupResponse result = statusUseCase.updadteStatus(
                    new UpdateGroupStatusRequest(GroupActive.INACTIVE), GROUP_ID, USERNAME);

            assertThat(result).isEqualTo(response);
            assertThat(group.getActive()).isEqualTo(GroupActive.INACTIVE);
            verify(authorizationService).authorizeOwner(GROUP_ID, USERNAME);
            verify(groupsRepository).save(group);
        }

        @Test
        void shouldReactivateGroupWhenOwner() {
            group.setActive(GroupActive.INACTIVE);
            when(groupsRepository.findById(GROUP_ID)).thenReturn(Optional.of(group));

            statusUseCase.updadteStatus(new UpdateGroupStatusRequest(GroupActive.ACTIVE), GROUP_ID, USERNAME);

            assertThat(group.getActive()).isEqualTo(GroupActive.ACTIVE);
        }

        @Test
        void shouldNotChangeStatusWhenActorIsNotOwner() {
            when(authorizationService.authorizeOwner(GROUP_ID, USERNAME))
                    .thenThrow(new AccessDeniedException("negado"));

            assertThatThrownBy(() -> statusUseCase.updadteStatus(
                    new UpdateGroupStatusRequest(GroupActive.INACTIVE), GROUP_ID, USERNAME))
                    .isInstanceOf(AccessDeniedException.class);

            verify(groupsRepository, never()).save(any(GroupsEntity.class));
        }
    }
}
