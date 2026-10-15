package com.vkm_backend.tournaments.usecase;

import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.domain.GroupMembers;
import com.vkm_backend.group.infra.persistence.web.dto.AccessPolicyResponse;
import com.vkm_backend.group.service.GroupAccessPolicy;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.teams.infra.mapper.TeamsMapper;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.teams.service.ListDetailTeamMembersService;
import com.vkm_backend.tournaments.domain.TournamentTeamsStatus;
import com.vkm_backend.tournaments.domain.Tournaments;
import com.vkm_backend.tournaments.domain.TournamentsStatus;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsTeamsEntity;
import com.vkm_backend.tournaments.infra.mapper.TournamentMapper;
import com.vkm_backend.tournaments.infra.repository.TournamentsRepository;
import com.vkm_backend.tournaments.infra.repository.TournamentsTeamsRepository;
import com.vkm_backend.tournaments.infra.web.dto.TournamentResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StartTournamentsUseCase {
    private final GroupAccessPolicy groupAccessPolicy;
    private final TournamentsRepository tournamentsRepository;
    private final TournamentsTeamsRepository tournamentsTeamsRepository;
    private final TeamsMapper teamsMapper;
    private final TournamentMapper tournamentMapper;
    private final ListDetailTeamMembersService listDetailTeamMembersService;
    private static final long MIN_TEAMS_TO_START = 4;

    public StartTournamentsUseCase(GroupAccessPolicy groupAccessPolicy, TournamentsRepository tournamentsRepository, TournamentsTeamsRepository tournamentsTeamsRepository, TeamsMapper teamsMapper, TournamentMapper tournamentMapper, ListDetailTeamMembersService listDetailTeamMembersService) {
        this.groupAccessPolicy = groupAccessPolicy;
        this.tournamentsRepository = tournamentsRepository;
        this.tournamentsTeamsRepository = tournamentsTeamsRepository;
        this.teamsMapper = teamsMapper;
        this.tournamentMapper = tournamentMapper;
        this.listDetailTeamMembersService = listDetailTeamMembersService;
    }

    @Transactional
    public TournamentResponse execute(Long groupId, Long tournamentId, String username) {
        AccessPolicyResponse access = groupAccessPolicy.authorizeAdminAndOwner(groupId, username);

    TournamentsEntity tournament = tournamentsRepository.findByIdAndGroupId(tournamentId, access.groups());

    if (tournament == null) {
        throw new BusinessException("Torneio não encontrado ou não pertence ao grupo: " + access.groups().getName());
    }

    validateStartOfTournament(tournament);

    tournament.setStatus(TournamentsStatus.IN_PROGRESS);
    TournamentsEntity savedTournament = tournamentsRepository.save(tournament);
        Tournaments tournamentStarted = tournamentMapper.toDomain(savedTournament);

    return tournamentMapper.toResponse(tournamentStarted);
}

    private void validateStartOfTournament(TournamentsEntity tournament) {
        Specification<TournamentsEntity> otherInProgress = (root, query, cb) -> cb.and(
                cb.equal(root.get("groupId"), tournament.getGroupId()),
                cb.equal(root.get("status"), TournamentsStatus.IN_PROGRESS)
        );

        if (tournamentsRepository.count(otherInProgress) > 0) {
            throw new BusinessException("Não é possível iniciar um novo torneio antes de finalizar ou cancelar o torneio já em andamento no grupo.");
        }

        Specification<TournamentsTeamsEntity> registeredTeams = (root, query, cb) -> cb.and(
                cb.equal(root.get("tournamentId"), tournament),
                cb.or(
                        cb.isNull(root.get("status")),
                        cb.equal(root.get("status"), TournamentTeamsStatus.ACTIVE)
                )
        );


        long teams = tournamentsTeamsRepository.count(registeredTeams);

        if (teams < MIN_TEAMS_TO_START) {
            throw new BusinessException("Não é possível iniciar o torneio: são necessários no mínimo " + MIN_TEAMS_TO_START
                    + " times registrados aprovados, mas há apenas " + teams + ".");
        }

        List<TournamentsTeamsEntity> TournamentTeams = tournamentsTeamsRepository.findAll(registeredTeams);
        if (TournamentTeams.isEmpty()) {
            throw new BusinessException("Não há times registrados aprovados para iniciar o torneio.");
        }

        List<TeamsEntity> teamsInconsistent = new ArrayList<>();
        String inconsistentMembers = "";
        for (TournamentsTeamsEntity team : TournamentTeams) {
            List<GroupMembers> groupMembers = listDetailTeamMembersService.execute(team.getTeamId().getId(), tournament.getGroupId().getId());
            for (GroupMembers member : groupMembers) {
                if (member.getStatus() != GroupMemberStatus.APPROVED) {
                    inconsistentMembers = "SIM";
                }
            }
            if (inconsistentMembers.equals("SIM")) {
                teamsInconsistent.add(team.getTeamId());
                inconsistentMembers = "";
            }
        }
        if (!teamsInconsistent.isEmpty()) {
            throw new BusinessException("Não é possível iniciar o torneio: os seguintes times possuem membros que não estão aprovados no grupo: " +
                    teamsInconsistent.stream().map(teamsMapper::toDomain).map(teamsMapper::toResponse).toList());
        }
    }

}
