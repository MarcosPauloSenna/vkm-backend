package com.vkm_backend.unit.mapper;

import com.vkm_backend.group.domain.GroupActive;
import com.vkm_backend.group.domain.Groups;
import com.vkm_backend.group.infra.mapper.GroupMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.mapper.EntityMapper;
import com.vkm_backend.infra.global.mapper.PageResponseMapper;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageResponseMapperTest {

    private final PageResponseMapper pageResponseMapper = new PageResponseMapper();
    private final EntityMapper<GroupsEntity, Groups, GroupResponse> groupMapper = new GroupMapper();

    @Test
    void shouldConvertPageUsingEntityMapperAndKeepPaginationMetadata() {
        Page<GroupsEntity> page = new PageImpl<>(List.of(group(1L, "A"), group(2L, "B")),
                PageRequest.of(0, 2), 5);

        PageResponse<GroupResponse> response = pageResponseMapper.toPageResponse(page, groupMapper);

        assertThat(response.content()).extracting(GroupResponse::name).containsExactly("A", "B");
        assertThat(response.totalElements()).isEqualTo(5);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.page()).isZero();
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.first()).isTrue();
        assertThat(response.hasNext()).isTrue();
        assertThat(response.hasPrevious()).isFalse();
    }

    @Test
    void shouldConvertPageUsingCustomFunction() {
        Page<GroupsEntity> page = new PageImpl<>(List.of(group(1L, "A")));

        PageResponse<String> response = pageResponseMapper.toPageResponse(page, GroupsEntity::getName);

        assertThat(response.content()).containsExactly("A");
        assertThat(response.totalElements()).isEqualTo(1);
    }

    @Test
    void shouldReturnEmptyResponseForEmptyPage() {
        PageResponse<GroupResponse> response = pageResponseMapper.toPageResponse(
                Page.<GroupsEntity>empty(PageRequest.of(0, 10)), groupMapper);

        assertThat(response.content()).isEmpty();
        assertThat(response.totalElements()).isZero();
    }

    @Test
    void entityMapperDefaultShouldChainEntityToDomainToResponse() {
        GroupResponse response = groupMapper.toResponseFromEntity(group(7L, "Volei"));

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.name()).isEqualTo("Volei");
        assertThat(response.active()).isEqualTo("ACTIVE");
        assertThat(response.owner()).isEqualTo("dono");
    }

    private GroupsEntity group(Long id, String name) {
        UserEntity owner = new UserEntity();
        owner.setUsername("dono");
        GroupsEntity entity = new GroupsEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setActive(GroupActive.ACTIVE);
        entity.setOwner(owner);
        return entity;
    }
}
