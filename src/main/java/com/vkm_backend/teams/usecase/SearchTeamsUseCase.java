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
import com.vkm_backend.teams.service.SearchTeamsService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SearchTeamsUseCase {


    private final GroupsRepository groupsRepository;
    private final TeamsMapper teamsMapper;
    private final PageResponseMapper pageResponseMapper;
    private final SearchTeamsService searchTeamsService;

    public SearchTeamsUseCase(GroupsRepository groupsRepository, TeamsMapper teamsMapper, PageResponseMapper pageResponseMapper, SearchTeamsService searchTeamsService) {
          this.groupsRepository = groupsRepository;
        this.teamsMapper = teamsMapper;
        this.pageResponseMapper = pageResponseMapper;
        this.searchTeamsService = searchTeamsService;
    }

    @Transactional
    public PageResponse<TeamsGroupResponse> execute(Long groupId,
                                                    TeamsSearchRequest request,
                                                    Pageable pageable) {

        GroupsEntity group = groupsRepository.findById(groupId)
                .orElseThrow(GroupNotFoundException::new);


        Page<TeamsEntity> teamsEntityList = searchTeamsService.searchTeams(group, request, pageable);

        return pageResponseMapper.toPageResponse(teamsEntityList,
                entity -> teamsMapper.toResponse(teamsMapper.toResponse(teamsMapper.toDomain(entity))));

    }
}
