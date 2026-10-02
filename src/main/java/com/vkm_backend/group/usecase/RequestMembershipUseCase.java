package com.vkm_backend.group.usecase;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.infra.mapper.GroupMemberMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.MembershipRequest;
import com.vkm_backend.group.infra.persistence.web.dto.MembershipResponse;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.GroupNotFoundException;
import com.vkm_backend.user.infra.persistence.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class RequestMembershipUseCase {

    private static final Duration REAPPLICATION_WAIT = Duration.ofHours(24);

    private final GroupsRepository groupsRepository;

    private final GroupMembersRepository groupMembersRepository;

    private final UserRepository userRepository;

    private final GroupMemberMapper groupMemberMapper;

    public RequestMembershipUseCase(GroupMembersRepository groupMembersRepository, GroupsRepository groupsRepository, UserRepository userRepository, GroupMemberMapper groupMemberMapper) {
        this.groupMembersRepository = groupMembersRepository;
        this.groupsRepository = groupsRepository;
        this.userRepository = userRepository;
        this.groupMemberMapper = groupMemberMapper;
    }

    @Transactional
    public MembershipResponse associate(MembershipRequest request) {

        if (!groupsRepository.existsById(request.groupId())) {
            throw new GroupNotFoundException();
        }

        var user = userRepository.findByUsername(request.username());

        Optional<GroupMembersEntity> existingMembership = groupMembersRepository
                .findByGroupId_IdAndUserId_Id(request.groupId(), user.getId());
        if (existingMembership.isPresent()) {
            return reapply(existingMembership.get());
        }

        GroupMembersEntity groupMembers = new GroupMembersEntity();
        groupMembers.setGroupId(groupsRepository.getReferenceById(request.groupId()));
        groupMembers.setUserId(user);
        groupMembers.setRole(GroupMemberRole.MEMBER);
        groupMembers.setStatus(GroupMemberStatus.PENDING);

        GroupMembersEntity groupMembersEntity = groupMembersRepository.save(groupMembers);

        return toResponse(groupMembersEntity);
    }

    private MembershipResponse reapply(GroupMembersEntity membership) {
        GroupMemberStatus currentStatus = membership.getStatus();

        if (currentStatus == GroupMemberStatus.CANCELLED || currentStatus == GroupMemberStatus.LEFT) {
            // Solicitação cancelada pelo próprio usuário ou saída voluntária: pode solicitar novamente de imediato.
            membership.setStatus(GroupMemberStatus.PENDING);
            membership.setRole(GroupMemberRole.MEMBER);
            membership.setApprovedAt(null);
            membership.setApprovedBy(null);
            return toResponse(groupMembersRepository.save(membership));
        }

        if (currentStatus != GroupMemberStatus.REJECTED) {
            throw new BusinessException("Usuario ja associado a este grupo.");
        }

        Instant rejectedAt = membership.getApprovedAt();
        if (rejectedAt == null) {
            throw new BusinessException("Não foi possível verificar quando a solicitação foi rejeitada.");
        }

        validateElegibility(rejectedAt.plus(REAPPLICATION_WAIT));
        membership.setStatus(GroupMemberStatus.PENDING);
        membership.setRole(GroupMemberRole.MEMBER);
        membership.setApprovedAt(null);
        membership.setApprovedBy(null);

        return toResponse(groupMembersRepository.save(membership));
    }

    private MembershipResponse toResponse(GroupMembersEntity membership) {
        return groupMemberMapper.toResponse(groupMemberMapper.toDomain(membership));
    }

    private void validateElegibility(Instant eligibleAt) {
        if (Instant.now().isBefore(eligibleAt)) {
            throw new BusinessException("Uma nova solicitação só pode ser feita após 24 horas da rejeição.");
        }
    }


}
