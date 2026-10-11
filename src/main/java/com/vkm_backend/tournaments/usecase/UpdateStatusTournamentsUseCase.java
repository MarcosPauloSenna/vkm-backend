package com.vkm_backend.tournaments.usecase;

import com.vkm_backend.group.infra.persistence.web.dto.AccessPolicyResponse;
import com.vkm_backend.group.service.GroupAccessPolicy;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.TournamentNotFoundException;
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
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Service
public class UpdateStatusTournamentsUseCase {

    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private static final long MIN_TEAMS_TO_START = 4;

    private static final Map<TournamentsStatus, Set<TournamentsStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(Map.of(
            TournamentsStatus.DRAFT, EnumSet.of(TournamentsStatus.REGISTRATION, TournamentsStatus.CANCELLED),
            TournamentsStatus.REGISTRATION, EnumSet.of(TournamentsStatus.IN_PROGRESS, TournamentsStatus.CANCELLED),
            TournamentsStatus.IN_PROGRESS, EnumSet.of(TournamentsStatus.FINISHED, TournamentsStatus.CANCELLED)
    ));

    private final GroupAccessPolicy groupAccessPolicy;
    private final TournamentsRepository tournamentsRepository;
    private final TournamentsTeamsRepository tournamentsTeamsRepository;
    private final TournamentMapper mapper;

    public UpdateStatusTournamentsUseCase(GroupAccessPolicy groupAccessPolicy,
                                          TournamentsRepository tournamentsRepository,
                                          TournamentsTeamsRepository tournamentsTeamsRepository,
                                          TournamentMapper mapper) {
        this.groupAccessPolicy = groupAccessPolicy;
        this.tournamentsRepository = tournamentsRepository;
        this.tournamentsTeamsRepository = tournamentsTeamsRepository;
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

        if (newStatus == TournamentsStatus.IN_PROGRESS) {
            validateStartOfTournament(tournament);
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
    }
}
