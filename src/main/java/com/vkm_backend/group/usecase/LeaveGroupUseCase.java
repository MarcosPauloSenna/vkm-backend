package com.vkm_backend.group.usecase;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.infra.mapper.GroupMemberMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.web.dto.MemberStatusResponse;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.MemberNotFoundException;
import com.vkm_backend.user.infra.persistence.UserEntity;
import com.vkm_backend.user.infra.persistence.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class LeaveGroupUseCase {

    private final UserRepository userRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final GroupMemberMapper mapper;

    public LeaveGroupUseCase(UserRepository userRepository, GroupMembersRepository groupMembersRepository, GroupMemberMapper mapper) {
        this.userRepository = userRepository;
        this.groupMembersRepository = groupMembersRepository;
        this.mapper = mapper;
    }

    @Transactional
    public MemberStatusResponse execute(Long groupId, String username) {

        UserEntity user = userRepository.findByUsername(username);

        Optional<GroupMembersEntity> memberFind = groupMembersRepository.findByGroupId_IdAndUserId_Id(groupId, user.getId());

        if (memberFind.isEmpty()) {
            throw new MemberNotFoundException("Membro não encontrado no grupo.");
        }

        GroupMembersEntity member = memberFind.get();

        GroupMemberStatus newStatus = resolveNewStatus(member);

        member.setStatus(newStatus);
        member.setApprovedBy(user);
        member.setApprovedAt(Instant.now());

        GroupMembersEntity updated = groupMembersRepository.save(member);

        return mapper.toStatusResponse(mapper.toDomain(updated));
    }

    private GroupMemberStatus resolveNewStatus(GroupMembersEntity member) {
        return switch (member.getStatus()) {
            case APPROVED -> {
                if (member.getRole() == GroupMemberRole.OWNER) {
                    throw new BusinessException(
                            "O proprietário deve transferir a titularidade do grupo antes de sair.");
                }
                yield GroupMemberStatus.LEFT;
            }
            case PENDING -> GroupMemberStatus.CANCELLED;
            case SUSPENDED -> throw new BusinessException(
                    "Membro suspenso não pode sair do grupo. É necessário reativação primeiro.");
            case REJECTED -> throw new BusinessException(
                    "Não há vínculo ativo neste grupo para sair.");
            case CANCELLED -> throw new BusinessException(
                    "Solicitação de entrada já foi cancelada anteriormente.");
            case LEFT -> throw new BusinessException("Usuário já saiu deste grupo.");
        };
    }
}
