package com.vkm_backend.teams.usecase;


import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.exceptions.GroupNotFoundException;
import com.vkm_backend.infra.global.exceptions.ValidationException;
import com.vkm_backend.infra.global.mapper.PageResponseMapper;
import com.vkm_backend.teams.infra.mapper.TeamsMapper;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamResponse;
import com.vkm_backend.teams.infra.persistence.web.dto.TeamsSearchRequest;
import com.vkm_backend.teams.service.SearchTeamsService;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import com.vkm_backend.user.infra.persistence.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SearchMyTeamsUseCase {

    private final UserRepository userRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final SearchTeamsService searchTeamsService;
    private final GroupsRepository groupsRepository;
    private final TeamsMapper teamsMapper;
    private final PageResponseMapper pageResponseMapper;


    public SearchMyTeamsUseCase(UserRepository userRepository, GroupMembersRepository groupMembersRepository, SearchTeamsService searchTeamsService, GroupsRepository groupsRepository, TeamsMapper teamsMapper, PageResponseMapper pageResponseMapper) {
        this.userRepository = userRepository;
        this.groupMembersRepository = groupMembersRepository;
        this.searchTeamsService = searchTeamsService;
        this.groupsRepository = groupsRepository;
        this.teamsMapper = teamsMapper;
        this.pageResponseMapper = pageResponseMapper;
    }

    @Transactional
    public PageResponse<TeamResponse> execute(Long groupId, String userName,
                                              TeamsSearchRequest request,
                                              Pageable pageable) {

        UserEntity user = userRepository.findByUsername(userName);
        if (user == null) {
            throw new ValidationException("Usuário não localizado");
        }
        GroupsEntity group = null;

        if (groupId != null) {
            group = groupsRepository.findById(groupId)
                    .orElseThrow(GroupNotFoundException::new);
        }

        List<GroupMembersEntity> groupMember = groupMembersRepository.findByUserId(user);

        request.memberIds().addAll(groupMember.stream().map(GroupMembersEntity::getId).toList());

        Page<TeamsEntity> teamsEntityList = searchTeamsService.searchTeams(group, request, pageable);

        return pageResponseMapper.toPageResponse(teamsEntityList,
                entity -> teamsMapper.toResponse(teamsMapper.toDomain(entity)));
    }
}
