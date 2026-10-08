package com.vkm_backend.teams.service;

import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.infra.global.specification.DynamicFilter;
import com.vkm_backend.infra.global.specification.DynamicSpecification;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.teams.infra.persistence.repository.TeamsRepository;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamsSearchRequest;
import com.vkm_backend.teams.infra.specification.TeamSpecification;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SearchTeamsService {

    private final TeamsRepository teamsRepository;


    public SearchTeamsService(TeamsRepository teamsRepository) {
        this.teamsRepository = teamsRepository;
    }

    @Transactional
    public Page<TeamsEntity> searchTeams(GroupsEntity groupId,
                                         TeamsSearchRequest request,
                                         Pageable pageable) {

        Specification<TeamsEntity> teamsEntitySpecification = TeamSpecification.hasMemberId(request.memberIds(),
                request.memberFilterMode());

        return teamsRepository.findAll(DynamicSpecification
                .<TeamsEntity>where(DynamicFilter.toEquals(groupId, "groupId"))
                .and(DynamicFilter.toLike(request.name(), "name"))
                .and(DynamicFilter.toEquals(request.id(), "id"))
                .and(DynamicFilter.toGreaterEqualTo(request.createdAtFrom(), "createdAt"))
                .and(DynamicFilter.toLessEqualTo(request.createdAtTo(), "createdAt"))
                .and(DynamicFilter.toEquals(request.size(), "teamSize"))
                .and(teamsEntitySpecification), pageable);
    }
}
