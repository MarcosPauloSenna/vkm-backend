package com.vkm_backend.tournaments.usecase;

import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.domain.GroupMembers;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.service.GroupAccessPolicy;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.TournamentNotFoundException;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.teams.infra.persistence.repository.TeamsRepository;
import com.vkm_backend.teams.service.ListDetailTeamMembersService;
import com.vkm_backend.tournaments.domain.TournamentTeamsStatus;
import com.vkm_backend.tournaments.domain.TournamentsTeams;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsTeamsEntity;
import com.vkm_backend.tournaments.infra.mapper.TournamentTeamsMapper;
import com.vkm_backend.tournaments.infra.repository.TournamentsRepository;
import com.vkm_backend.tournaments.infra.repository.TournamentsTeamsRepository;
import com.vkm_backend.tournaments.infra.web.dto.TournamentsTeamsResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AproveTournamentTeamsUseCase {

    private final ListDetailTeamMembersService listDetailTeamMembersService;
    private final TournamentsTeamsRepository tournamentsTeamsRepository;
    private final TournamentsRepository tournamentsRepository;
    private final GroupAccessPolicy groupAccessPolicy;
    private final TeamsRepository teamsRepository;
    private final TournamentTeamsMapper tournamentTeamsMapper;
    private static final List<TournamentTeamsStatus> ALLOWED_STATUSES = List.of(TournamentTeamsStatus.ELIMINATED,
            TournamentTeamsStatus.CHAMPION, TournamentTeamsStatus.WITHDRAWN);

    public AproveTournamentTeamsUseCase(ListDetailTeamMembersService listDetailTeamMembersService, TournamentsTeamsRepository tournamentsTeamsRepository, TournamentsRepository tournamentsRepository, GroupAccessPolicy groupAccessPolicy, TeamsRepository teamsRepository, TournamentTeamsMapper tournamentTeamsMapper) {
        this.listDetailTeamMembersService = listDetailTeamMembersService;
        this.tournamentsTeamsRepository = tournamentsTeamsRepository;
        this.tournamentsRepository = tournamentsRepository;
        this.groupAccessPolicy = groupAccessPolicy;
        this.teamsRepository = teamsRepository;
        this.tournamentTeamsMapper = tournamentTeamsMapper;
    }

    @Transactional
    public TournamentsTeamsResponse execute(Long teamId, Long tournamentId, Long groupId, String userName) {

        GroupsEntity group = groupAccessPolicy.authorizeAdminAndOwner(groupId, userName).groups();

        TournamentsEntity tournament = tournamentsRepository.findByIdAndGroupId(tournamentId, group);
        if (tournament == null) {
            throw new TournamentNotFoundException("Torneio não encontrado");
        }

        TeamsEntity team = teamsRepository.findByIdAndGroupId(teamId, group);
        if (team == null) {
            throw new BusinessException("Time não encontrado");
        }

        TournamentsTeamsEntity tournamentTeam = tournamentsTeamsRepository.findByTeamIdAndTournamentId(team, tournament);

        if (tournamentTeam == null) {
            throw new BusinessException("Time não está inscrito no torneio");
        }

        List<GroupMembers> groupMembers = listDetailTeamMembersService.execute(teamId, groupId);

        for (GroupMembers member : groupMembers) {
            if (!member.getStatus().equals(GroupMemberStatus.APPROVED)) {
                throw new BusinessException("1 ou mais membros do time não estão ativos no grupo");
            }
        }

        if (tournamentTeam.getStatus() == TournamentTeamsStatus.ACTIVE) {
            throw new BusinessException("Time já está aprovado no torneio");
        }

        if (ALLOWED_STATUSES.contains(tournamentTeam.getStatus())) {
            throw new BusinessException("Time não pode ser aprovado no torneio devido ao seu status atual");
        }


        tournamentTeam.setStatus(TournamentTeamsStatus.ACTIVE);

        TournamentsTeamsEntity tournamentsTeamsSaved = tournamentsTeamsRepository.save(tournamentTeam);

        TournamentsTeams tournamentsTeamsApproved = tournamentTeamsMapper.toDomain(tournamentsTeamsSaved);

        return tournamentTeamsMapper.toResponse(tournamentsTeamsApproved);
    }
}
