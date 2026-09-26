package com.vkm_backend.group.usecase;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.domain.GroupMembers;
import com.vkm_backend.group.domain.Groups;
import com.vkm_backend.group.infra.mapper.GroupMemberMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.MembershipRequest;
import com.vkm_backend.group.infra.persistence.web.dto.MembershipResponse;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.user.infra.persistence.UserRepository;
import jakarta.transaction.Transactional;

public class RequestMembershipUseCase {

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

        GroupMembersEntity groupMembers = new GroupMembersEntity();

        Long userId = userRepository.findByUsername(request.username()).getId();


        if (groupMembersRepository.existsByGroupIdAndUserId(request.groupId(), userId)) {
            throw new BusinessException("Usuario ja associado a este grupo.");
        }


        groupMembers.setGroupId(groupsRepository.getReferenceById(request.groupId()));
        groupMembers.setUseId(userRepository.getReferenceById(userId));
        groupMembers.setRole(GroupMemberRole.MEMBER);
        groupMembers.setStatus(GroupMemberStatus.PENDING);

        GroupMembersEntity groupMembersEntity = groupMembersRepository.save(groupMembers);

        GroupMembers memberSaved = groupMemberMapper.toDomain(groupMembersEntity);

        return groupMemberMapper.toResponse(memberSaved);


    }


}
