package com.vkm_backend.teams.usecase;

import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.service.GroupMemberAccessPolicy;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.teams.infra.mapper.TeamsMapper;
import com.vkm_backend.teams.infra.persistence.entities.TeamMembersEntity;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.teams.infra.persistence.repository.TeamMembersRepository;
import com.vkm_backend.teams.infra.persistence.repository.TeamsRepository;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamsGroupResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class UpadateNameTeamUseCase {
    private final GroupMemberAccessPolicy groupMemberAccessPolicy;
    private final TeamMembersRepository teamMembersRepository;
    private final TeamsRepository teamsRepository;
    private final TeamsMapper teamsMapper;

    public UpadateNameTeamUseCase(GroupMemberAccessPolicy groupMemberAccessPolicy, TeamMembersRepository teamMembersRepository, TeamsRepository teamsRepository, TeamsMapper teamsMapper) {
        this.groupMemberAccessPolicy = groupMemberAccessPolicy;
        this.teamMembersRepository = teamMembersRepository;
        this.teamsRepository = teamsRepository;
        this.teamsMapper = teamsMapper;
    }

    @Transactional
    public TeamsGroupResponse execute(Long teamId,
                                      String newName,
                                      Long groupId,
                                      String userName) {

        GroupMembersEntity groupMember = groupMemberAccessPolicy.authorize(groupId, userName);

        List<TeamMembersEntity> teamMembers = teamMembersRepository.findByTeamsId_Id(teamId);

        List<Long> teamMemberIds = new ArrayList<>();

        for (TeamMembersEntity teamMember : teamMembers) {
            teamMemberIds.add(teamMember.getGroupMembersId().getId());
        }

        if (!teamMemberIds.contains(groupMember.getId())) {
            throw new BusinessException("Usuário não é membro do time");
        }

        TeamsEntity team = teamsRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException("Time não encontrado"));

        team.setName(newName);

        TeamsEntity updatedNameTeam = teamsRepository.save(team);

        return teamsMapper.toResponse(teamsMapper.toResponse(teamsMapper.toDomain(updatedNameTeam)));
    }
}
