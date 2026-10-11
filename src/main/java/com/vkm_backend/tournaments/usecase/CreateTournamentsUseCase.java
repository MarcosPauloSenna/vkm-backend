package com.vkm_backend.tournaments.usecase;

import com.vkm_backend.group.infra.persistence.web.dto.AccessPolicyResponse;
import com.vkm_backend.group.service.GroupAccessPolicy;
import com.vkm_backend.tournaments.domain.TournamentsStatus;
import com.vkm_backend.tournaments.infra.entities.TournamentsEntity;
import com.vkm_backend.tournaments.infra.mapper.TournamentMapper;
import com.vkm_backend.tournaments.infra.repository.TournamentsRepository;
import com.vkm_backend.tournaments.infra.web.dto.CreateTournamentRequest;
import com.vkm_backend.tournaments.infra.web.dto.TournamentResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateTournamentsUseCase {

    private final TournamentsRepository tournamentsRepository;
    private final GroupAccessPolicy groupAccessPolicy;
    private final TournamentMapper mapper;

    public CreateTournamentsUseCase(TournamentsRepository tournamentsRepository, GroupAccessPolicy groupAccessPolicy, TournamentMapper mapper) {
        this.tournamentsRepository = tournamentsRepository;
        this.groupAccessPolicy = groupAccessPolicy;
        this.mapper = mapper;
    }

    @Transactional
    public TournamentResponse execute(String userMame, CreateTournamentRequest request, Long groupId) {
        AccessPolicyResponse responseAccess = groupAccessPolicy.authorizeAdminAndOwner(groupId, userMame);

        TournamentsEntity tournamentEntity = new TournamentsEntity();
        tournamentEntity.setName(request.name());
        tournamentEntity.setDescription(request.description());
        tournamentEntity.setGroupId(responseAccess.groups());
        tournamentEntity.setStartDate(request.startDate());
        tournamentEntity.setEndDate(request.endDate());
        tournamentEntity.setFormat(request.format());
        tournamentEntity.setStatus(TournamentsStatus.DRAFT);
        TournamentsEntity savedTournament = tournamentsRepository.save(tournamentEntity);

        return mapper.toResponse(mapper.toDomain(savedTournament));
    }
}
