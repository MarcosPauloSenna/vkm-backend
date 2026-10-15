package com.vkm_backend.tournaments.usecase;

import com.vkm_backend.group.infra.persistence.web.dto.AccessPolicyResponse;
import com.vkm_backend.group.service.GroupAccessPolicy;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.mapper.PageResponseMapper;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import com.vkm_backend.tournaments.infra.mapper.TournamentMapper;
import com.vkm_backend.tournaments.infra.repository.TournamentsRepository;
import com.vkm_backend.tournaments.infra.web.dto.SearchTournamentRequest;
import com.vkm_backend.tournaments.infra.web.dto.TournamentResponse;
import com.vkm_backend.tournaments.service.SearchTournamentsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchTournamentsUseCase {

   private final GroupAccessPolicy groupAccessPolicy;
    private final PageResponseMapper pageResponseMapper;
    private final TournamentMapper tournamentMapper;
    private final SearchTournamentsService searchTournamentsService;

    public SearchTournamentsUseCase(GroupAccessPolicy groupAccessPolicy, PageResponseMapper pageResponseMapper, TournamentMapper tournamentMapper, SearchTournamentsService searchTournamentsService) {
        this.groupAccessPolicy = groupAccessPolicy;
        this.pageResponseMapper = pageResponseMapper;
        this.tournamentMapper = tournamentMapper;
        this.searchTournamentsService = searchTournamentsService;
    }

    @Transactional(readOnly = true)
    public PageResponse<TournamentResponse> execute(Long groupId, SearchTournamentRequest request, Pageable pageable, String userName) {

        AccessPolicyResponse response = groupAccessPolicy.authorize(groupId, userName);

        Page<TournamentsEntity> tournamentsList = searchTournamentsService.search(response.groups(), request, pageable);

        return pageResponseMapper.toPageResponse(tournamentsList, tournaments -> tournamentMapper.toResponse(tournamentMapper.toDomain(tournaments)));
    }
}
