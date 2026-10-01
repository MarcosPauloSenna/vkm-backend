package com.vkm_backend.group.usecase;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.infra.mapper.GroupMemberMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.web.dto.MemberFindResponse;
import com.vkm_backend.group.infra.persistence.web.dto.MemberStatusRequest;
import com.vkm_backend.group.service.GroupMemberAuthorizationService;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.ValidationException;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class UpadateStatusMemberUseCase {
    private  final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private final GroupMemberAuthorizationService authorizationService;
    private final GroupMembersRepository membersRepository;
    private final GroupMemberMapper mapper;

    public UpadateStatusMemberUseCase(GroupMemberAuthorizationService authorizationService, GroupMembersRepository membersRepository, GroupMemberMapper mapper) {
        this.authorizationService = authorizationService;
        this.membersRepository = membersRepository;
        this.mapper = mapper;
    }

    @Transactional
    public MemberFindResponse updateStatusMember(Long groupId, Long memberId, String username, MemberStatusRequest request){

         GroupMembersEntity membersAuthorized = authorizationService.authorizeAdminAndOwner(groupId, username);


        Optional<GroupMembersEntity> memberFind = membersRepository.findById(memberId);

        GroupMembersEntity member = valitedUpdateStatus(request.status(), memberFind, membersAuthorized);

        GroupMembersEntity memberStatusUpdated = membersRepository.save(member);

        return mapper.toFindResponse(mapper.toDomain(memberStatusUpdated));



    }

    private @NonNull GroupMembersEntity valitedUpdateStatus(GroupMemberStatus status, Optional<GroupMembersEntity> memberFind, GroupMembersEntity membersAuthorized) {

        if (memberFind.isEmpty()){
            throw new ValidationException("Membro não localizado!");
        }

        GroupMembersEntity member = memberFind.get();

        if ((member.getRole().equals(GroupMemberRole.ADMIN) || member.getRole().equals(GroupMemberRole.OWNER))
                && membersAuthorized.getRole().equals(GroupMemberRole.ADMIN)){
            throw new BusinessException("Nivel de permissão insuficiente. Operação não permitida!");
        }

        if (member.getStatus().equals(status)){
            throw new BusinessException("Membro "+member.getUserId().getName()+", ja esta com status "+ status +"!");
        }




        member.setApprovedBy(membersAuthorized.getUserId());

        member.setStatus(status);

        member.setApprovedAt(Instant.now());
        return member;
    }
}
