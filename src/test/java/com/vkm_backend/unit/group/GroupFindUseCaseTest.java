package com.vkm_backend.unit.group;

import com.vkm_backend.group.infra.mapper.GroupMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.group.infra.persistence.web.dto.GroupSearchRequest;
import com.vkm_backend.group.service.GetGroupsFromEntityToDomain;
import com.vkm_backend.group.usecase.GroupFindUseCase;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.exceptions.IllegalFieldArgumentException;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class GroupFindUseCaseTest {

    @Mock
    private GroupsRepository groupsRepository;
    @Mock
    private GetGroupsFromEntityToDomain fromEntityToDomain;
    @Mock
    private GroupMapper groupMapper;
    @InjectMocks
    private GroupFindUseCase useCase;

    @Test
    void shouldSearchGroupsAndReturnPageResponse() {
        Page<GroupsEntity> page = new PageImpl<>(List.of(new GroupsEntity()));
        PageResponse<GroupResponse> expected = new PageResponse<>(List.of(), 0, 10, 1, 1, true, true, false, false);
        when(groupsRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(fromEntityToDomain.execute(page, groupMapper)).thenReturn(expected);

        PageResponse<GroupResponse> response = useCase.search(
                new GroupSearchRequest(null, "volei", null, "cruz", null), PageRequest.of(0, 10));

        assertThat(response).isSameAs(expected);
    }

    @Test
    void shouldTranslateAllowedSortFields() {
        when(groupsRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());
        when(fromEntityToDomain.execute(any(), any())).thenReturn(null);

        useCase.search(new GroupSearchRequest(null, null, null, null, null),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "cidade")));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(groupsRepository).findAll(any(Specification.class), captor.capture());
        assertThat(captor.getValue().getSort().getOrderFor("city")).isNotNull();
    }

    @Test
    void shouldRejectSortFieldOutsideWhitelist() {
        assertThatThrownBy(() -> useCase.search(new GroupSearchRequest(null, null, null, null, null),
                PageRequest.of(0, 10, Sort.by("owner"))))
                .isInstanceOf(IllegalFieldArgumentException.class);

        verify(groupsRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }
}
