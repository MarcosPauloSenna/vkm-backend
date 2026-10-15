package com.vkm_backend.tournaments.service;


import com.vkm_backend.infra.global.specification.DynamicFilter;
import com.vkm_backend.infra.global.specification.DynamicSpecification;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsTeamsEntity;
import com.vkm_backend.tournaments.infra.repository.TournamentsTeamsRepository;
import com.vkm_backend.tournaments.infra.specification.TournamentTeamSpecification;
import com.vkm_backend.tournaments.infra.web.dto.SearchTournamentTeamsRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SearchTournamentTeamsService {

    private final TournamentsTeamsRepository tournamentsTeamsRepository;

    public SearchTournamentTeamsService(TournamentsTeamsRepository tournamentsTeamsRepository) {
        this.tournamentsTeamsRepository = tournamentsTeamsRepository;

    }


    public Page<TournamentsTeamsEntity> searchTournamentTeams(TournamentsEntity tournaments, SearchTournamentTeamsRequest request, Pageable pageable) {

        Specification<TournamentsTeamsEntity> teamsEntitySpecification = TournamentTeamSpecification.hasTeamsId(request.nameTeam());


        return tournamentsTeamsRepository.findAll(DynamicSpecification
                .<TournamentsTeamsEntity>where(DynamicFilter.toEquals(tournaments, "tournamentId"))
                .and(DynamicFilter.toEquals(request.tournamentTeamsId(), "id"))
                .and(DynamicFilter.toEquals(request.groupName(), "groupName"))
                .and(DynamicFilter.toEquals(request.seed(), "seed"))
                .and(DynamicFilter.toEquals(request.status(), "status"))
                .and(DynamicFilter.toGreaterEqualTo(request.createdAtFrom(), "createdAt"))
                .and(DynamicFilter.toLessEqualTo(request.createdAtTo(), "createdAt"))
                .and(teamsEntitySpecification), pageable);

    }


}
