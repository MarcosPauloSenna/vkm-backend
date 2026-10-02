package com.vkm_backend.group.usecase;

import com.vkm_backend.group.domain.Groups;
import com.vkm_backend.group.infra.mapper.GroupMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.group.infra.persistence.web.dto.UpdateGroupRequest;
import com.vkm_backend.group.service.GroupAccessPolicy;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.GroupNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.Optional;

@Service
public class UpdateGroupUseCase {

    private final GroupsRepository groupsRepository;
    private final GroupMapper groupMapper;
    private final GroupAccessPolicy authorizationService;


    public UpdateGroupUseCase(GroupsRepository groupsRepository, GroupMapper groupMapper, GroupAccessPolicy authorizationService) {
        this.groupsRepository = groupsRepository;
        this.groupMapper = groupMapper;
        this.authorizationService = authorizationService;
    }


    @Transactional
    public GroupResponse updateGroup(UpdateGroupRequest request, Long groupId, String username) {
        Optional<GroupsEntity> groupFind = groupsRepository.findById(groupId);

        if (groupFind.isEmpty()) {
            throw new GroupNotFoundException();
        }

        if (request.name() == null
                && request.description() == null
                && request.city() == null
                && request.state() == null) {

            throw new BusinessException(
                    "Nenhum dado informado para atualização.");
        }

        authorizationService.authorizeAdminAndOwner(groupId, username);

        GroupsEntity group = groupFind.get();

        if (request.name() != null) {
            group.setName(request.name());
        }

        if (request.description() != null) {
            group.setDescription(request.description());
        }

        if (request.city() != null) {
            group.setCity(request.city());
        }

        if (request.state() != null) {
            group.setState(request.state());
        }

        Groups groupSave = groupMapper.toDomain(groupsRepository.save(group));

        return groupMapper.toResponse(groupSave);
    }
}
