package com.vkm_backend.group.usecase;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.infra.mapper.GroupMemberMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.web.dto.MemberRoleRequest;
import com.vkm_backend.group.infra.persistence.web.dto.MemberStatusResponse;
import com.vkm_backend.group.service.GroupMemberAuthorizationService;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.MemberNotFoundException;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UpdateRoleMemberUseCase {

    private final GroupMemberAuthorizationService authorizationService;

    private final GroupMemberMapper mapper;

    private final GroupMembersRepository membersRepository;

    public UpdateRoleMemberUseCase(GroupMemberAuthorizationService authorizationService, GroupMemberMapper mapper, GroupMembersRepository membersRepository) {
        this.authorizationService = authorizationService;
        this.mapper = mapper;
        this.membersRepository = membersRepository;
    }


    @Transactional
    public MemberStatusResponse execute(Long groupId, Long memberId, String username, MemberRoleRequest request) {

        GroupMembersEntity membersAuthorized = authorizationService.authorizeOwner(groupId, username);

        Optional<GroupMembersEntity> memberFind = membersRepository.findByIdAndGroupId_Id(memberId, groupId);

        GroupMembersEntity updatedMember = validatedUpdateStatus(request.role(), memberFind, membersAuthorized);
        GroupMembersEntity memberRoleUpdated = membersRepository.save(updatedMember);

        if (request.role().equals(GroupMemberRole.OWNER)) {
            revokeOwnerRole(membersAuthorized);
        }

        return mapper.toStatusResponse(mapper.toDomain(memberRoleUpdated));
    }

    private @NonNull GroupMembersEntity validatedUpdateStatus(GroupMemberRole role, Optional<GroupMembersEntity> memberFind, GroupMembersEntity membersAuthorized) {

        if (memberFind.isEmpty()) {
            throw new MemberNotFoundException("Membro não localizado!");
        }

        GroupMembersEntity member = memberFind.get();

        if (!member.getStatus().equals(GroupMemberStatus.APPROVED)) {
            throw new BusinessException("Operação não permitida. Membro "
                    + member.getUserId().getName() + ", com status "
                    + member.getStatus().name() + "!");
        }

        if (member.getRole().equals(role)) {
            throw new BusinessException("Membro " + member.getUserId().getName() + ", ja esta com Função " + role.name() + "!");
        }

        member.setApprovedBy(membersAuthorized.getUserId());

        member.setRole(role);

        return member;
    }

    private void revokeOwnerRole(@NonNull GroupMembersEntity memberRevoking) {


      memberRevoking.setRole(GroupMemberRole.MEMBER);

      membersRepository.save(memberRevoking);

    }
}
