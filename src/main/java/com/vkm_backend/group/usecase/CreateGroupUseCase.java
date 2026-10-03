package com.vkm_backend.group.usecase;

import com.vkm_backend.group.domain.Groups;
import com.vkm_backend.group.infra.mapper.GroupMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.group.infra.persistence.web.dto.CreateGroupRequest;
import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.group.service.MembershipRegistrar;
import com.vkm_backend.infra.global.exceptions.ConflictException;
import com.vkm_backend.infra.global.exceptions.ResourceNotFoundException;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import com.vkm_backend.user.infra.persistence.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

@Service
public class CreateGroupUseCase {

    private final GroupsRepository groupsRepository;

    private final GroupMapper groupMapper;

    private final MembershipRegistrar membershipRegistrar;

    private final UserRepository userRepository;

    private  final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

    public CreateGroupUseCase(GroupsRepository groupsRepository, GroupMapper groupMapper, MembershipRegistrar membershipRegistrar, UserRepository userRepository) {
        this.groupsRepository = groupsRepository;
        this.groupMapper = groupMapper;
        this.membershipRegistrar = membershipRegistrar;
        this.userRepository = userRepository;
    }

    @Transactional
    public GroupResponse createGroup(CreateGroupRequest dados, String username ) {


        if (groupsRepository.existsByNameAndCityAndState(dados.name(), dados.city(), dados.state())) {
            throw new ConflictException("Nome de grupo ja existe nesta cidade, digite outro nome.");
        }

        UserEntity owner = userRepository.findByUsername(username);
        if (owner == null) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }

        Groups newGroup = groupMapper.toDomain(dados);
        newGroup.setOwner(owner);

        GroupsEntity entity = groupMapper.toEntity(newGroup);

        GroupsEntity groupSavedEntity = groupsRepository.save(entity);

        Groups groupSavedDomain = groupMapper.toDomain(groupSavedEntity);

        membershipRegistrar.registerOwner(groupSavedEntity, owner);

        return groupMapper.toResponse(groupSavedDomain);


    }
}
