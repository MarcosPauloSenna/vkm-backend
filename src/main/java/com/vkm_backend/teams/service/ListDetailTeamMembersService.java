package com.vkm_backend.teams.service;

import com.vkm_backend.group.domain.GroupMembers;
import com.vkm_backend.group.infra.mapper.GroupMemberMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.teams.infra.persistence.entities.TeamMembersEntity;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.teams.infra.persistence.repository.TeamMembersRepository;
import com.vkm_backend.teams.infra.persistence.repository.TeamsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ListDetailTeamMembersService {

    private final TeamMembersRepository teamMembersRepository;
    private final TeamsRepository teamsRepository;
    private final GroupsRepository groupsRepository;
    private final GroupMemberMapper groupMemberMapper;



    public ListDetailTeamMembersService(TeamMembersRepository teamMembersRepository, TeamsRepository teamsRepository, GroupsRepository groupsRepository, GroupMemberMapper groupMemberMapper) {
        this.teamMembersRepository = teamMembersRepository;
        this.teamsRepository = teamsRepository;
        this.groupsRepository = groupsRepository;
        this.groupMemberMapper = groupMemberMapper;

    }

    public List<GroupMembers> execute(Long teamId, Long groupId) {

        GroupsEntity group = groupsRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Grupo não encontrado"));

        TeamsEntity team = teamsRepository.findByIdAndGroupId(teamId, group);

        Collection<TeamMembersEntity> teamMembers = teamMembersRepository.findByTeamsId(team);
        List<GroupMembers> groupMembersList = new ArrayList<>();
        for (TeamMembersEntity teamMember : teamMembers) {
            groupMembersList.add(groupMemberMapper.toDomain(teamMember.getGroupMembersId()));
        }

        return groupMembersList;

    }
}
