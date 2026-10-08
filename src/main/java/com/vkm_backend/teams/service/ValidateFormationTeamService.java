package com.vkm_backend.teams.service;

import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.infra.global.exceptions.GroupNotFoundException;
import com.vkm_backend.infra.global.exceptions.ValidationException;
import com.vkm_backend.teams.infra.persistence.web.dto.ValidationResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
@Service
public class ValidateFormationTeamService {

    private final GroupsRepository groupsRepository;
    private final GroupMembersRepository groupMembersRepository;

    public ValidateFormationTeamService(GroupsRepository groupsRepository, GroupMembersRepository groupMembersRepository) {
        this.groupsRepository = groupsRepository;
        this.groupMembersRepository = groupMembersRepository;
    }

    public ValidationResponse validateMemberInGroupId(Collection<Long> memberId, Long groupId) {
        Optional<GroupsEntity> group = groupsRepository.findById(groupId);
        if (group.isEmpty()) {
            throw new GroupNotFoundException();
        }

        Collection<GroupMembersEntity> teamMembers = new ArrayList<>();

        for (Long id : memberId) {
            Optional<GroupMembersEntity> member = groupMembersRepository.findByIdAndGroupId_Id(id, groupId);
            if (member.isEmpty()) {
                throw new ValidationException("1 ou mais membros não pertence ao grupo " + group.get().getName());
            }

            if (!member.get().getStatus().equals(GroupMemberStatus.APPROVED)) {
                throw new ValidationException("1 ou mais membros do time não estão aprovados no grupo " + group.get().getName() + "!");
            }

            if (teamMembers.contains(member.get())) {
                throw new ValidationException(
                        "Não é permitido adicionar o mesmo membro mais de uma vez ao time."
                );
            }

            teamMembers.add(member.get());
        }


        GroupsEntity groupEntity = group.get();

        return new ValidationResponse(groupEntity, teamMembers);
    }


}
