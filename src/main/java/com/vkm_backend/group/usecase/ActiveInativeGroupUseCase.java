package com.vkm_backend.group.usecase;

import com.vkm_backend.group.domain.Groups;
import com.vkm_backend.group.infra.mapper.GroupMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.group.infra.persistence.web.dto.UpdateGroupStatusRequest;
import com.vkm_backend.group.service.GroupMemberAuthorizationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ActiveInativeGroupUseCase {

    private final GroupsRepository groupsRepository;
    private final GroupMapper groupMapper;
    private final GroupMemberAuthorizationService authorizationService;

    public ActiveInativeGroupUseCase(GroupMemberAuthorizationService authorizationService, GroupsRepository groupsRepository, GroupMapper groupMapper) {
        this.authorizationService = authorizationService;
        this.groupsRepository = groupsRepository;
        this.groupMapper = groupMapper;
    }

    @Transactional
    public GroupResponse updadteStatus(UpdateGroupStatusRequest request, Long groupId, String username){
        authorizationService.authorizeOwner(groupId, username);

        Optional<GroupsEntity> group = groupsRepository.findById(groupId);

        GroupsEntity inactiveActiveGroup = group.get();

        inactiveActiveGroup.setActive(request.status());

        Groups activeGroupSave = groupMapper.toDomain(groupsRepository.save(inactiveActiveGroup));

        return groupMapper.toResponse(activeGroupSave);
    }

}
