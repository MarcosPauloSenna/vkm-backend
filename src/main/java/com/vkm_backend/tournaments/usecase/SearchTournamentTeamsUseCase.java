package com.vkm_backend.tournaments.usecase;

import com.vkm_backend.group.infra.persistence.web.dto.AccessPolicyResponse;
import com.vkm_backend.group.service.GroupAccessPolicy;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.exceptions.TournamentNotFoundException;
import com.vkm_backend.infra.global.mapper.PageResponseMapper;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsTeamsEntity;
import com.vkm_backend.tournaments.infra.mapper.TournamentTeamsMapper;
import com.vkm_backend.tournaments.infra.repository.TournamentsRepository;
import com.vkm_backend.tournaments.infra.web.dto.SearchTournamentTeamsRequest;
import com.vkm_backend.tournaments.infra.web.dto.TournamentsTeamsResponse;
import com.vkm_backend.tournaments.service.SearchTournamentTeamsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class SearchTournamentTeamsUseCase {

    private final SearchTournamentTeamsService searchTournamentTeamsService;
    private final TournamentsRepository tournamentsRepository;
    private final GroupAccessPolicy groupAccessPolicy;
    private final PageResponseMapper pageResponseMapper;
    private final TournamentTeamsMapper tournamentTeamsMapper;

    public SearchTournamentTeamsUseCase(SearchTournamentTeamsService searchTournamentTeamsService, TournamentsRepository tournamentsRepository, GroupAccessPolicy groupAccessPolicy, PageResponseMapper pageResponseMapper, TournamentTeamsMapper tournamentTeamsMapper) {
        this.searchTournamentTeamsService = searchTournamentTeamsService;
        this.tournamentsRepository = tournamentsRepository;
        this.groupAccessPolicy = groupAccessPolicy;
        this.pageResponseMapper = pageResponseMapper;
        this.tournamentTeamsMapper = tournamentTeamsMapper;
    }

    public PageResponse<TournamentsTeamsResponse> execute(SearchTournamentTeamsRequest request,
                                                          Pageable pageable,
                                                          Long tournamentId,
                                                          Long groupId,
                                                          String userName) {
        AccessPolicyResponse accessPolicyResponse = groupAccessPolicy.authorize(groupId, userName);

        TournamentsEntity tournaments = tournamentsRepository.findByIdAndGroupId(tournamentId, accessPolicyResponse.groups());

        if (tournaments == null) {
            throw new TournamentNotFoundException("Torneio não encontrado ou não pertence ao grupo.");
        }

        Page<TournamentsTeamsEntity> tournamentTeamsPage = searchTournamentTeamsService.searchTournamentTeams(tournaments,
                request,
                pageable);

        return pageResponseMapper.toPageResponse(tournamentTeamsPage,
                team -> tournamentTeamsMapper.toResponse(tournamentTeamsMapper.toDomain(team)));
    }
}
