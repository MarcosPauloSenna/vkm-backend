package com.vkm_backend.tournaments.usecase;

import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.domain.GroupMembers;
import com.vkm_backend.group.infra.persistence.web.dto.AccessPolicyResponse;
import com.vkm_backend.group.service.GroupAccessPolicy;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.TournamentNotFoundException;
import com.vkm_backend.teams.service.ListDetailTeamMembersService;
import com.vkm_backend.tournaments.domain.TournamentTeamsStatus;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class UpdateStatusTournamentsUseCase {

    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private static final long MIN_TEAMS_TO_START = 4;

    private static final Map<TournamentsStatus, Set<TournamentsStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(Map.of(
            TournamentsStatus.DRAFT, EnumSet.of(TournamentsStatus.REGISTRATION, TournamentsStatus.CANCELLED),
            TournamentsStatus.REGISTRATION, EnumSet.of(TournamentsStatus.DRAFT, TournamentsStatus.CANCELLED),
            TournamentsStatus.IN_PROGRESS, EnumSet.of(TournamentsStatus.FINISHED, TournamentsStatus.CANCELLED)
    ));

    private final GroupAccessPolicy groupAccessPolicy;
    private final TournamentsRepository tournamentsRepository;
    private final TournamentMapper mapper;


    public UpdateStatusTournamentsUseCase(GroupAccessPolicy groupAccessPolicy,
                                          TournamentsRepository tournamentsRepository,
                                          TournamentMapper mapper) {
        this.groupAccessPolicy = groupAccessPolicy;
        this.tournamentsRepository = tournamentsRepository;
        this.mapper = mapper;
    }

    @Transactional
    public TournamentResponse execute(Long groupId, Long tournamentId, TournamentsStatus newStatus, String username) {
        AccessPolicyResponse access = groupAccessPolicy.authorizeAdminAndOwner(groupId, username);

        TournamentsEntity tournament = tournamentsRepository.findByIdAndGroupId(tournamentId, access.groups());
        if (tournament == null) {
            throw new TournamentNotFoundException("Torneio não encontrado ou não pertence ao grupo: " + access.groups().getName());
        }

        if (newStatus == null) {
            throw new BusinessException("O novo status do torneio deve ser informado.");
        }

        TournamentsStatus current = tournament.getStatus();

        if (current == newStatus) {
            throw new BusinessException("O torneio já está com o status " + newStatus + ".");
        }

        validateTransition(current, newStatus);

        if (newStatus == TournamentsStatus.REGISTRATION) {
            validateStartOfRegistration(tournament);
        }

        tournament.setStatus(newStatus);
        TournamentsEntity saved = tournamentsRepository.save(tournament);

        return mapper.toResponse(mapper.toDomain(saved));
    }

    private void validateTransition(TournamentsStatus from, TournamentsStatus to) {
        if (!ALLOWED_TRANSITIONS.getOrDefault(from, Set.of()).contains(to)) {
            throw new BusinessException("Transição de status não permitida: " + from + " -> " + to + ".");
        }
    }

    private void validateStartOfRegistration(TournamentsEntity tournament) {
        LocalDate startDate = tournament.getStartDate();

        if (startDate == null || !LocalDate.now(ZONE).isBefore(startDate)) {
            throw new BusinessException("Não é possível abrir as inscrições: a data atual deve ser anterior à data de início do torneio. "
                    + "Altere a data de início do torneio para uma data futura.");
        }
    }


}
