package com.vkm_backend.tournaments.usecase;

import com.vkm_backend.group.infra.persistence.web.dto.AccessPolicyResponse;
import com.vkm_backend.group.service.GroupAccessPolicy;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.TeamsNotFoundExcepton;
import com.vkm_backend.infra.global.exceptions.TournamentNotFoundException;

import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.teams.infra.persistence.repository.TeamsRepository;
import com.vkm_backend.tournaments.domain.TournamentTeamsStatus;
import com.vkm_backend.tournaments.domain.TournamentsStatus;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsTeamsEntity;
import com.vkm_backend.tournaments.infra.mapper.TournamentTeamsMapper;
import com.vkm_backend.tournaments.infra.repository.TournamentsRepository;
import com.vkm_backend.tournaments.infra.repository.TournamentsTeamsRepository;
import com.vkm_backend.tournaments.infra.web.dto.RegisterTournamentsTeamsRequest;
import com.vkm_backend.tournaments.infra.web.dto.TournamentsTeamsResponse;

import java.time.Instant;

public class RegisterTournamentsTeamsUseCase {

    private final TournamentTeamsMapper tournamentTeamsMapper;
    private final GroupAccessPolicy groupAccessPolicy;
    private final TournamentsRepository tournamentsRepository;
    private final TeamsRepository teamsRepository;
    private final TournamentsTeamsRepository tournamentsTeamsRepository;

    public RegisterTournamentsTeamsUseCase(TournamentTeamsMapper tournamentTeamsMapper, GroupAccessPolicy groupAccessPolicy, TournamentsRepository tournamentsRepository, TeamsRepository teamsRepository, TournamentsTeamsRepository tournamentsTeamsRepository) {
        this.tournamentTeamsMapper = tournamentTeamsMapper;
        this.groupAccessPolicy = groupAccessPolicy;
        this.tournamentsRepository = tournamentsRepository;
        this.teamsRepository = teamsRepository;
        this.tournamentsTeamsRepository = tournamentsTeamsRepository;
    }

    public TournamentsTeamsResponse register(RegisterTournamentsTeamsRequest request, Long tournamentId, String userName, Long groupId) {

        AccessPolicyResponse responseAccess = groupAccessPolicy.authorize(groupId, userName);

        TournamentsEntity tournament = tournamentsRepository.findByIdAndGroupId(tournamentId, responseAccess.groups());

        if (tournament == null) {
            throw new TournamentNotFoundException("Torneio não encontrado ou não pertence ao grupo: " + responseAccess.groups().getName());
        }

        if (!tournament.getStatus().equals(TournamentsStatus.REGISTRATION)) {
            throw new BusinessException("Torneio não esta em fase de inscrição ainda!");
        }

        TeamsEntity team = teamsRepository.findByIdAndGroupId(request.teamId(), responseAccess.groups());

        if (team == null) {
            throw new TeamsNotFoundExcepton("Time não encontrado ou não pertence ao grupo: " + responseAccess.groups().getName());
        }

        TournamentsTeamsEntity tournamentTeam = new TournamentsTeamsEntity();
        tournamentTeam.setTournamentId(tournament);
        tournamentTeam.setTeamId(team);
        tournamentTeam.setCreatedAt(Instant.now());
        TournamentsTeamsEntity registeredTournamentTeam = tournamentsTeamsRepository.save(tournamentTeam);

        return tournamentTeamsMapper.toResponse(tournamentTeamsMapper.toDomain(registeredTournamentTeam));
    }
}
