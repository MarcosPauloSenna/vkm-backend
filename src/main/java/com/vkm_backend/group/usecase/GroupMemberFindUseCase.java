package com.vkm_backend.group.usecase;

import com.vkm_backend.group.infra.mapper.GroupMemberMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.web.dto.GroupMemberFindRequest;
import com.vkm_backend.group.infra.persistence.web.dto.MemberStatusResponse;
import com.vkm_backend.group.service.GroupAccessPolicy;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.mapper.PageResponseMapper;
import com.vkm_backend.infra.global.specification.DynamicFilter;
import com.vkm_backend.infra.global.specification.DynamicSpecification;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import com.vkm_backend.user.infra.persistence.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupMemberFindUseCase {

    private final GroupMembersRepository groupMembersRepository;
    private final GroupMemberMapper mapper;
    private final GroupAccessPolicy authorizationService;
    private final UserRepository userRepository;
    private final PageResponseMapper pageResponseMapper;

    public GroupMemberFindUseCase(GroupMembersRepository groupMembersRepository,
                                  GroupMemberMapper mapper,
                                  GroupAccessPolicy authorizationService,
                                  UserRepository userRepository,
                                  PageResponseMapper pageResponseMapper) {
        this.groupMembersRepository = groupMembersRepository;
        this.mapper = mapper;
        this.authorizationService = authorizationService;
        this.userRepository = userRepository;
        this.pageResponseMapper = pageResponseMapper;
    }

    @Transactional
    public PageResponse<MemberStatusResponse> searchMembers(GroupMemberFindRequest request,
                                                            Long groupId,
                                                            String username,
                                                            Pageable pageable){

        authorizationService.authorize(groupId, username);

        pageable = SortWhitelist.validate(
                pageable,
                GroupMemberSortFields.SORT_FIELDS
        );

        List<UserEntity> user = userRepository.findByName(request.name());


        Page<GroupMembersEntity> groupsMemberEntityList = groupMembersRepository.findAll(DynamicSpecification
                .<GroupMembersEntity>where(DynamicFilter.toEquals(request.id(), "id"))
                .and(DynamicFilter.toContains(user, "userId"))
                .and(DynamicFilter.toContains(request.role(), "role"))
                .and(DynamicFilter.toContains(request.status(), "status"))
                .and(DynamicFilter.toEquals(groupId, "id", "groupId")), pageable);


        return pageResponseMapper.toPageResponse(groupsMemberEntityList,
                entity -> mapper.toStatusResponse(mapper.toDomain(entity)));

    }
}
