package com.vkm_backend.group.usecase;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.infra.mapper.GroupMemberMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.web.dto.MemberStatusResponse;
import com.vkm_backend.group.infra.persistence.web.dto.MemberStatusRequest;
import com.vkm_backend.group.service.GroupAccessPolicy;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.MemberNotFoundException;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class UpadateStatusMemberUseCase {
    private  final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private final GroupAccessPolicy authorizationService;
    private final GroupMembersRepository membersRepository;
    private final GroupMemberMapper mapper;

    // Transições que ADMIN e OWNER podem realizar.
    private static final Map<GroupMemberStatus, Set<GroupMemberStatus>> COMMON_TRANSITIONS = new EnumMap<>(Map.of(
            GroupMemberStatus.PENDING, EnumSet.of(GroupMemberStatus.APPROVED, GroupMemberStatus.REJECTED),
            GroupMemberStatus.APPROVED, EnumSet.of(GroupMemberStatus.SUSPENDED)
    ));

    // Transições extras permitidas somente ao OWNER (segunda via, sem precisar de nova solicitação).
    // SUSPENDED <-> REJECTED permanece proibido mesmo para o OWNER.
    private static final Map<GroupMemberStatus, Set<GroupMemberStatus>> OWNER_ONLY_TRANSITIONS = new EnumMap<>(Map.of(
            GroupMemberStatus.REJECTED, EnumSet.of(GroupMemberStatus.APPROVED, GroupMemberStatus.PENDING),
            GroupMemberStatus.SUSPENDED, EnumSet.of(GroupMemberStatus.APPROVED, GroupMemberStatus.PENDING)
    ));

    public UpadateStatusMemberUseCase(GroupAccessPolicy authorizationService, GroupMembersRepository membersRepository, GroupMemberMapper mapper) {
        this.authorizationService = authorizationService;
        this.membersRepository = membersRepository;
        this.mapper = mapper;
    }

    @Transactional
    public MemberStatusResponse updateStatusMember(Long groupId, Long memberId, String username, MemberStatusRequest request){

         GroupMembersEntity membersAuthorized = authorizationService.authorizeAdminAndOwner(groupId, username);


        Optional<GroupMembersEntity> memberFind = membersRepository.findByIdAndGroupId_Id(memberId, groupId);

        GroupMembersEntity member = validatedUpdateStatus(request.status(), memberFind, membersAuthorized);

        GroupMembersEntity memberStatusUpdated = membersRepository.save(member);

        return mapper.toStatusResponse(mapper.toDomain(memberStatusUpdated));



    }

    private @NonNull GroupMembersEntity validatedUpdateStatus(GroupMemberStatus status, Optional<GroupMembersEntity> memberFind, GroupMembersEntity membersAuthorized) {

        if (memberFind.isEmpty()){
            throw new MemberNotFoundException("Membro não localizado!");
        }

        GroupMembersEntity member = memberFind.get();

        if ((member.getRole().equals(GroupMemberRole.ADMIN) || member.getRole().equals(GroupMemberRole.OWNER))
                && membersAuthorized.getRole().equals(GroupMemberRole.ADMIN)){
            throw new BusinessException("Nivel de permissão insuficiente. Operação não permitida!");
        }

        if (member.getStatus().equals(status)){
            throw new BusinessException("Membro "+member.getUserId().getName()+", ja esta com status "+ status +"!");
        }

        validateStatusTransition(member.getStatus(), status, membersAuthorized.getRole());

        member.setApprovedBy(membersAuthorized.getUserId());

        member.setStatus(status);

        member.setApprovedAt(Instant.now());
        return member;
    }

    private void validateStatusTransition(GroupMemberStatus from, GroupMemberStatus to, GroupMemberRole actingRole) {
        boolean allowedForAdminOrOwner = COMMON_TRANSITIONS.getOrDefault(from, Set.of()).contains(to);
        boolean allowedOnlyForOwner = OWNER_ONLY_TRANSITIONS.getOrDefault(from, Set.of()).contains(to);

        if (!allowedForAdminOrOwner && !allowedOnlyForOwner) {
            throw new BusinessException("Transição de status não permitida: " + from + " -> " + to + ".");
        }

        if (allowedOnlyForOwner && !allowedForAdminOrOwner && actingRole != GroupMemberRole.OWNER) {
            throw new BusinessException("Apenas o proprietário do grupo pode realizar esta alteração de status.");
        }
    }
}
