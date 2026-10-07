package com.vkm_backend.teams.usecase;

import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.exceptions.GroupNotFoundException;
import com.vkm_backend.infra.global.mapper.PageResponseMapper;
import com.vkm_backend.infra.global.specification.DynamicFilter;
import com.vkm_backend.infra.global.specification.DynamicSpecification;
import com.vkm_backend.teams.infra.mapper.TeamsMapper;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.teams.infra.persistence.repository.TeamsRepository;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamResponse;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamsGroupResponse;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamsSearchRequest;
import com.vkm_backend.teams.infra.specification.TeamSpecification;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SearchTeamsUseCase {

    private final TeamsRepository teamsRepository;
    private final GroupsRepository groupsRepository;
    private final TeamsMapper teamsMapper;
    private final PageResponseMapper pageResponseMapper;


    public SearchTeamsUseCase(TeamsRepository teamsRepository, GroupsRepository groupsRepository, TeamsMapper teamsMapper, PageResponseMapper pageResponseMapper) {
        this.teamsRepository = teamsRepository;
        this.groupsRepository = groupsRepository;
        this.teamsMapper = teamsMapper;
        this.pageResponseMapper = pageResponseMapper;
    }

    @Transactional
    public PageResponse<TeamsGroupResponse> execute(Long groupId,
                                                    TeamsSearchRequest request,
                                                    Pageable pageable) {

        Specification<TeamsEntity> teamsEntitySpecification = TeamSpecification.hasMemberId(request.memberIds());

        GroupsEntity group = groupsRepository.findById(groupId)
                .orElseThrow(GroupNotFoundException::new);

        Page<TeamsEntity> teamsEntityList = teamsRepository.findAll(DynamicSpecification
                        .<TeamsEntity>where(DynamicFilter.toEquals(group, "groupId"))
                        .and(DynamicFilter.toLike(request.name(), "name"))
                        .and(DynamicFilter.toEquals(request.id(), "id"))
                        .and(DynamicFilter.toGreaterEqualTo(request.createdAtFrom(), "createdAt"))
                        .and(DynamicFilter.toLessEqualTo(request.createdAtTo(), "createdAt"))
                        .and(DynamicFilter.toEquals(request.size(), "teamSize"))
                        .and(teamsEntitySpecification), pageable);



        return pageResponseMapper.toPageResponse(teamsEntityList,
                entity -> teamsMapper.toResponse(teamsMapper.toResponse(teamsMapper.toDomain(entity))));

    }
}
