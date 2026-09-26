package com.vkm_backend.group.usecase;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.domain.Groups;
import com.vkm_backend.group.infra.mapper.GroupMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.CreateGroupRequest;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.group.infra.persistence.web.dto.MembershipRequest;
import com.vkm_backend.group.infra.persistence.web.dto.MembershipResponse;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.ConflictException;
import com.vkm_backend.infra.global.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;

import java.util.Optional;

public class CreateGroupUseCase {

    private final GroupsRepository groupsRepository;

    private final GroupMapper groupMapper;

    private final GroupMembersRepository groupMembersRepository;

    private final RequestMembershipUseCase requestMembershipUseCase;

    public CreateGroupUseCase(GroupsRepository groupsRepository, GroupMapper groupMapper, GroupMembersRepository groupMembersRepository, RequestMembershipUseCase requestMembershipUseCase) {
        this.groupsRepository = groupsRepository;
        this.groupMapper = groupMapper;
        this.groupMembersRepository = groupMembersRepository;
        this.requestMembershipUseCase = requestMembershipUseCase;
    }

    @Transactional
    public GroupResponse createGroup(CreateGroupRequest dados, String username ) {


        if (groupsRepository.existsByNameAndCityAndState(dados.name(), dados.city(), dados.state())) {
            throw new ConflictException("Nome de grupo ja existe nesta cidade, digite outro nome.");
        }

        Groups newGroup = groupMapper.toDomain(dados);

        GroupsEntity entity = groupMapper.toEntity(newGroup);

        GroupsEntity groupSavedEntity = groupsRepository.save(entity);

        Groups groupSavedDomain = groupMapper.toDomain(groupSavedEntity);

        MembershipResponse response = requestMembershipUseCase
                .associate(new MembershipRequest(groupSavedEntity.getId(),
                                                 username));

        Optional<GroupMembersEntity> groupMember = groupMembersRepository.findById(response.id());

        if (groupMember.isEmpty()) {
            throw new ResourceNotFoundException("Falha na operação. grupo com Id" + response.id() + " não localizado.");
        }

        GroupMembersEntity member = groupMember.get();

        member.setStatus(GroupMemberStatus.APPROVED);
        member.setRole(GroupMemberRole.OWNER);


        groupMembersRepository.save(member);

        return groupMapper.toResponse(groupSavedDomain);


    }
}
