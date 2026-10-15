package com.vkm_backend.tournaments.usecase;

import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.web.dto.AccessPolicyResponse;
import com.vkm_backend.group.service.GroupAccessPolicy;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.TeamsNotFoundExcepton;
import com.vkm_backend.infra.global.exceptions.TournamentNotFoundException;

import com.vkm_backend.teams.infra.persistence.entities.TeamMembersEntity;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.teams.infra.persistence.repository.TeamMembersRepository;
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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Collection;

@Service
public class RegisterTournamentsTeamsUseCase {

    private final TournamentTeamsMapper tournamentTeamsMapper;
    private final GroupAccessPolicy groupAccessPolicy;
    private final TournamentsRepository tournamentsRepository;
    private final TeamsRepository teamsRepository;
    private final TournamentsTeamsRepository tournamentsTeamsRepository;
    private final TeamMembersRepository teamMembersRepository;

    public RegisterTournamentsTeamsUseCase(TournamentTeamsMapper tournamentTeamsMapper, GroupAccessPolicy groupAccessPolicy, TournamentsRepository tournamentsRepository, TeamsRepository teamsRepository, TournamentsTeamsRepository tournamentsTeamsRepository, TeamMembersRepository teamMembersRepository) {
        this.tournamentTeamsMapper = tournamentTeamsMapper;
        this.groupAccessPolicy = groupAccessPolicy;
        this.tournamentsRepository = tournamentsRepository;
        this.teamsRepository = teamsRepository;
        this.tournamentsTeamsRepository = tournamentsTeamsRepository;
        this.teamMembersRepository = teamMembersRepository;
    }

    @Transactional
    public TournamentsTeamsResponse register(RegisterTournamentsTeamsRequest request, Long tournamentId, String userName, Long groupId) {

        AccessPolicyResponse responseAccess = groupAccessPolicy.authorize(groupId, userName);

        TournamentsEntity tournament = tournamentsRepository.findByIdAndGroupId(tournamentId, responseAccess.groups());

        validationTournament(tournament, responseAccess.groups());

        TeamsEntity team = teamsRepository.findByIdAndGroupId(request.teamId(), responseAccess.groups());

        validationTeam(team, responseAccess.groups(), tournament);

        TournamentsTeamsEntity tournamentTeam = new TournamentsTeamsEntity();
        tournamentTeam.setTournamentId(tournament);
        tournamentTeam.setTeamId(team);
        tournamentTeam.setCreatedAt(Instant.now());
        tournamentTeam.setStatus(TournamentTeamsStatus.PENDING);
        TournamentsTeamsEntity registeredTournamentTeam = tournamentsTeamsRepository.save(tournamentTeam);

        return tournamentTeamsMapper.toResponse(tournamentTeamsMapper.toDomain(registeredTournamentTeam));
    }

    public void validationTournament(TournamentsEntity tournament, GroupsEntity group) {

        if (tournament == null) {
            throw new TournamentNotFoundException("Torneio não encontrado ou não pertence ao grupo: " + group.getName());
        }

        if (tournament.getStatus() != TournamentsStatus.REGISTRATION) {
            throw new BusinessException("Torneio não esta em fase de inscrição ainda!");
        }
    }

    public void validationTeam(TeamsEntity team, GroupsEntity group, TournamentsEntity tournament) {

        if (team == null) {
            throw new TeamsNotFoundExcepton("Time não encontrado ou não pertence ao grupo: " + group.getName());
        }

        if (tournamentsTeamsRepository.existsByTeamIdAndTournamentId(team, tournament)) {
            throw new BusinessException("O time já está registrado no torneio.");
        }

        Collection<TeamMembersEntity> teamMembers = teamMembersRepository.findByTeamsId(team);
        for (TeamMembersEntity member : teamMembers) {
            if (!member.getGroupMembersId().getStatus().equals(GroupMemberStatus.APPROVED)) {
                throw new BusinessException("Todos os membros do time devem estar com status \"aprovado\" no grupo para se inscrever no torneio.");
            }
        }
    }
}
