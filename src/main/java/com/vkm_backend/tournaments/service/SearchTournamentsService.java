package com.vkm_backend.tournaments.service;

import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.infra.global.specification.DynamicFilter;
import com.vkm_backend.infra.global.specification.DynamicSpecification;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import com.vkm_backend.tournaments.infra.repository.TournamentsRepository;
import com.vkm_backend.tournaments.infra.web.dto.SearchTournamentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class SearchTournamentsService {
    private final TournamentsRepository tournamentsRepository;

    public SearchTournamentsService(TournamentsRepository tournamentsRepository) {
        this.tournamentsRepository = tournamentsRepository;
    }


    public Page<TournamentsEntity> search(GroupsEntity groupId, SearchTournamentRequest request, Pageable pageable) {

        return tournamentsRepository.findAll(DynamicSpecification
                .<TournamentsEntity>where(DynamicFilter.toEquals(groupId, "groupId"))
                .and(DynamicFilter.toEquals(request.id(), "id"))
                .and(DynamicFilter.toLike(request.name(), "name"))
                .and(DynamicFilter.toLike(request.description(), "description"))
                .and(DynamicFilter.toEquals(request.status(), "status"))
                .and(DynamicFilter.toGreaterEqualTo(request.startDateFrom(), "startDateFrom"))
                .and(DynamicFilter.toLessEqualTo(request.startDateTo(), "startDateTo"))
                .and(DynamicFilter.toGreaterEqualTo(request.endDateFrom(), "endDateFrom"))
                .and(DynamicFilter.toLessEqualTo(request.endDateTo(), "endDateTo"))
                .and(DynamicFilter.toEquals(request.format(), "format"))
                .and(DynamicFilter.toGreaterEqualTo(request.createdAtFrom(), "createdAtFrom"))
                .and(DynamicFilter.toLessEqualTo(request.createdAtTo(), "createdAtTo")), pageable);
    }
}
