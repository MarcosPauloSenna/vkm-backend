package com.vkm_backend.unit.group;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.domain.GroupMembers;
import com.vkm_backend.group.infra.mapper.GroupMemberMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.web.dto.GroupMemberFindRequest;
import com.vkm_backend.group.infra.persistence.web.dto.MemberStatusResponse;
import com.vkm_backend.group.service.GroupMemberAuthorizationService;
import com.vkm_backend.group.usecase.GroupMemberFindUseCase;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.exceptions.IllegalFieldArgumentException;
import com.vkm_backend.infra.global.exceptions.MemberNotFoundException;
import com.vkm_backend.infra.global.mapper.PageResponseMapper;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import com.vkm_backend.user.infra.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class GroupMemberFindUseCaseTest {

    private static final Long GROUP_ID = 1L;
    private static final String USERNAME = "admin";

    @Mock
    private GroupMembersRepository groupMembersRepository;
    @Mock
    private GroupMemberMapper mapper;
    @Mock
    private GroupMemberAuthorizationService authorizationService;
    @Mock
    private UserRepository userRepository;
    @Spy
    private PageResponseMapper pageResponseMapper = new PageResponseMapper();
    @InjectMocks
    private GroupMemberFindUseCase useCase;

    private GroupMembersEntity memberEntity;

    @BeforeEach
    void setUp() {
        memberEntity = new GroupMembersEntity();
        memberEntity.setId(7L);
        memberEntity.setGroupId(new GroupsEntity());
        memberEntity.setUserId(new UserEntity());
        memberEntity.setRole(GroupMemberRole.MEMBER);
        memberEntity.setStatus(GroupMemberStatus.APPROVED);
    }

    @Test
    void shouldReturnPagedMembersOfTheGroup() {
        Instant start = Instant.now();
        GroupMembers domain = new GroupMembers();
        MemberStatusResponse dto = new MemberStatusResponse(7L, "Maria", "MEMBER", "APPROVED", start);

        when(userRepository.findByName(any())).thenReturn(List.of());
        when(groupMembersRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(memberEntity), PageRequest.of(0, 10), 1));
        when(mapper.toDomain(memberEntity)).thenReturn(domain);
        when(mapper.toStatusResponse(domain)).thenReturn(dto);

        PageResponse<MemberStatusResponse> response = useCase.searchMembers(
                new GroupMemberFindRequest(null, null, null, null), GROUP_ID, USERNAME, PageRequest.of(0, 10));

        assertThat(response.content()).containsExactly(dto);
        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.first()).isTrue();
        assertThat(response.last()).isTrue();
        verify(authorizationService).authorize(GROUP_ID, USERNAME);
    }

    @Test
    void shouldReturnEmptyPageWhenNoMemberMatchesFilters() {
        when(userRepository.findByName("Inexistente")).thenReturn(List.of());
        when(groupMembersRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(Page.empty(PageRequest.of(0, 10)));

        PageResponse<MemberStatusResponse> response = useCase.searchMembers(
                new GroupMemberFindRequest(99L, "Inexistente", List.of(GroupMemberRole.ADMIN),
                        List.of(GroupMemberStatus.PENDING)), GROUP_ID, USERNAME, PageRequest.of(0, 10));

        assertThat(response.content()).isEmpty();
        assertThat(response.totalElements()).isZero();
    }

    @Test
    void shouldTranslateAllowedSortFieldsToEntityProperties() {
        when(userRepository.findByName(any())).thenReturn(List.of());
        when(groupMembersRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        useCase.searchMembers(new GroupMemberFindRequest(null, null, null, null), GROUP_ID, USERNAME,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "nome")));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(groupMembersRepository).findAll(any(Specification.class), captor.capture());
        assertThat(captor.getValue().getSort().getOrderFor("userId.name")).isNotNull();
        assertThat(captor.getValue().getSort().getOrderFor("userId.name").getDirection())
                .isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void shouldRejectSortFieldOutsideWhitelist() {
        assertThatThrownBy(() -> useCase.searchMembers(new GroupMemberFindRequest(null, null, null, null),
                GROUP_ID, USERNAME, PageRequest.of(0, 10, Sort.by("password"))))
                .isInstanceOf(IllegalFieldArgumentException.class);

        verify(groupMembersRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void shouldNotSearchWhenRequesterIsNotMemberOfGroup() {
        when(authorizationService.authorize(GROUP_ID, USERNAME))
                .thenThrow(new MemberNotFoundException("Usuário não é membro deste grupo."));

        assertThatThrownBy(() -> useCase.searchMembers(new GroupMemberFindRequest(null, null, null, null),
                GROUP_ID, USERNAME, PageRequest.of(0, 10)))
                .isInstanceOf(MemberNotFoundException.class);

        verifyNoInteractions(groupMembersRepository);
    }
}
