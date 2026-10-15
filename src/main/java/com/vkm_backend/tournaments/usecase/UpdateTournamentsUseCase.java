package com.vkm_backend.tournaments.usecase;

import com.vkm_backend.group.infra.persistence.web.dto.AccessPolicyResponse;
import com.vkm_backend.group.service.GroupAccessPolicy;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.TournamentNotFoundException;
import com.vkm_backend.tournaments.domain.TournamentsStatus;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import com.vkm_backend.tournaments.infra.mapper.TournamentMapper;
import com.vkm_backend.tournaments.infra.repository.TournamentsRepository;
import com.vkm_backend.tournaments.infra.web.dto.CreateTournamentRequest;
import com.vkm_backend.tournaments.infra.web.dto.TournamentResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateTournamentsUseCase {
    private final TournamentsRepository tournamentsRepository;
    private final GroupAccessPolicy groupAccessPolicy;
    private final TournamentMapper mapper;
    private static final List<TournamentsStatus> ALLOWED_STATUSES = List.of(TournamentsStatus.DRAFT,
            TournamentsStatus.REGISTRATION);

    public UpdateTournamentsUseCase(TournamentsRepository tournamentsRepository, GroupAccessPolicy groupAccessPolicy, TournamentMapper mapper) {
        this.tournamentsRepository = tournamentsRepository;
        this.groupAccessPolicy = groupAccessPolicy;
        this.mapper = mapper;
    }

    @Transactional
    public TournamentResponse execute(CreateTournamentRequest request, Long groupId, Long tournamentId, String userName) {

        AccessPolicyResponse responseAccess = groupAccessPolicy.authorizeAdminAndOwner(groupId, userName);

        TournamentsEntity tournamentEntity = tournamentsRepository.findByIdAndGroupId(tournamentId, responseAccess.groups());
        if (tournamentEntity == null) {

            throw  new TournamentNotFoundException("Torneio não encontrado ou não " +
                    "grupo especificado.");
        }

        if (!ALLOWED_STATUSES.contains(tournamentEntity.getStatus())) {
            throw new BusinessException("Torneio não está em um status permitido para atualização.");
        }

        tournamentEntity.setName(request.name());
        tournamentEntity.setDescription(request.description());
        tournamentEntity.setStartDate(request.startDate());
        tournamentEntity.setEndDate(request.endDate());
        tournamentEntity.setFormat(request.format());

        TournamentsEntity updatedTournament = tournamentsRepository.save(tournamentEntity);
        return mapper.toResponse(mapper.toDomain(updatedTournament));
    }
}
