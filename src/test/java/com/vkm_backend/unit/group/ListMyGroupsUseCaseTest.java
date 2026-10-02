package com.vkm_backend.unit.group;

import com.vkm_backend.group.infra.mapper.GroupMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.group.infra.persistence.web.dto.GroupSearchRequest;
import com.vkm_backend.group.service.GetGroupsFromEntityToDomain;
import com.vkm_backend.group.usecase.ListMyGroupsUseCase;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.exceptions.IllegalFieldArgumentException;
import com.vkm_backend.infra.global.exceptions.ValidationException;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import com.vkm_backend.user.infra.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ListMyGroupsUseCaseTest {

    private static final String USERNAME = "player";

    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupsRepository groupsRepository;
    @Mock
    private GroupMembersRepository groupMembersRepository;
    @Mock
    private GroupMapper groupMapper;
    @Mock
    private GetGroupsFromEntityToDomain fromEntityToDomain;
    @InjectMocks
    private ListMyGroupsUseCase useCase;

    private UserEntity user;
    private final PageResponse<GroupResponse> expected = new PageResponse<>(List.of(), 0, 10, 0, 0, true, true, false, false);

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setUsername(USERNAME);
    }

    @Test
    void shouldListOnlyGroupsOfAuthenticatedUser() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(groupMembersRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(membership(10L), membership(20L)));
        Page<GroupsEntity> page = new PageImpl<>(List.of(group(10L), group(20L)));
        when(groupsRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(fromEntityToDomain.execute(page, groupMapper)).thenReturn(expected);

        PageResponse<GroupResponse> response = useCase.execute(emptyRequest(), USERNAME, PageRequest.of(0, 10));

        assertThat(response).isSameAs(expected);
    }

    @Test
    void shouldFilterBySpecificGroupAmongUserGroups() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(groupMembersRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(membership(10L), membership(20L)));
        Page<GroupsEntity> page = new PageImpl<>(List.of(group(20L)));
        when(groupsRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(fromEntityToDomain.execute(page, groupMapper)).thenReturn(expected);

        PageResponse<GroupResponse> response = useCase.execute(
                new GroupSearchRequest(20L, null, null, null, null), USERNAME, PageRequest.of(0, 10));

        assertThat(response).isSameAs(expected);
        verify(groupsRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void shouldReturnEmptyPageWhenRequestedGroupIsNotOfTheUser() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(groupMembersRepository.findAll(any(Specification.class))).thenReturn(List.of(membership(10L)));
        when(fromEntityToDomain.execute(any(Page.class), eq(groupMapper))).thenReturn(expected);

        PageResponse<GroupResponse> response = useCase.execute(
                new GroupSearchRequest(999L, null, null, null, null), USERNAME, PageRequest.of(0, 10));

        assertThat(response).isSameAs(expected);
        verify(groupsRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void shouldFailWhenUserHasNoGroups() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(groupMembersRepository.findAll(any(Specification.class))).thenReturn(List.of());

        assertThatThrownBy(() -> useCase.execute(emptyRequest(), USERNAME, PageRequest.of(0, 10)))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("não possui grupos");

        verify(groupsRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void shouldFailWhenAuthenticatedUserDoesNotExist() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);

        assertThatThrownBy(() -> useCase.execute(emptyRequest(), USERNAME, PageRequest.of(0, 10)))
                .isInstanceOf(ValidationException.class);

        verify(groupMembersRepository, never()).findAll(any(Specification.class));
    }

    @Test
    void shouldTranslateAllowedSortFields() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(groupMembersRepository.findAll(any(Specification.class))).thenReturn(List.of(membership(10L)));
        when(groupsRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());
        when(fromEntityToDomain.execute(any(Page.class), eq(groupMapper))).thenReturn(expected);

        useCase.execute(emptyRequest(), USERNAME, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "nome")));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(groupsRepository).findAll(any(Specification.class), captor.capture());
        assertThat(captor.getValue().getSort().getOrderFor("name")).isNotNull();
    }

    @Test
    void shouldRejectSortFieldOutsideWhitelist() {
        assertThatThrownBy(() -> useCase.execute(emptyRequest(), USERNAME,
                PageRequest.of(0, 10, Sort.by("owner"))))
                .isInstanceOf(IllegalFieldArgumentException.class);
    }

    private GroupSearchRequest emptyRequest() {
        return new GroupSearchRequest(null, null, null, null, null);
    }

    private GroupMembersEntity membership(Long groupId) {
        GroupMembersEntity member = new GroupMembersEntity();
        member.setGroupId(group(groupId));
        member.setUserId(user);
        return member;
    }

    private GroupsEntity group(Long id) {
        GroupsEntity group = new GroupsEntity();
        group.setId(id);
        return group;
    }
}
