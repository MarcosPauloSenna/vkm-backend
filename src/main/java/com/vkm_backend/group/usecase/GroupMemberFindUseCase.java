package com.vkm_backend.group.usecase;

import com.vkm_backend.group.domain.GroupMembers;
import com.vkm_backend.group.infra.mapper.GroupMemberMapper;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.web.dto.GroupMemberFindRequest;
import com.vkm_backend.group.infra.persistence.web.dto.MemberStatusResponse;
import com.vkm_backend.group.service.GroupMemberAuthorizationService;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.specification.DynamicFilter;
import com.vkm_backend.infra.global.specification.DynamicSpecification;
import com.vkm_backend.user.infra.persistence.UserEntity;
import com.vkm_backend.user.infra.persistence.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupMemberFindUseCase {

    private final GroupMembersRepository groupMembersRepository;
    private final GroupMemberMapper mapper;
    private final GroupMemberAuthorizationService authorizationService;
    private final UserRepository userRepository;

    public GroupMemberFindUseCase(GroupMembersRepository groupMembersRepository,
                                  GroupMemberMapper mapper,
                                  GroupMemberAuthorizationService authorizationService,
                                  UserRepository userRepository) {
        this.groupMembersRepository = groupMembersRepository;
        this.mapper = mapper;
        this.authorizationService = authorizationService;
        this.userRepository = userRepository;
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
                .and(DynamicFilter.toContains(user, "user_id"))
                .and(DynamicFilter.toContains(request.role(), "role"))
                .and(DynamicFilter.toContains(request.status(), "status"))
                .and(DynamicFilter.toEquals(groupId, "group_id")), pageable);


        Page<GroupMembers> pageDomain = groupsMemberEntityList.map(mapper::toDomain);
        Page<MemberStatusResponse> responsePage = pageDomain.map(mapper::toStatusResponse);





        return new PageResponse<>(responsePage.getContent(),
                responsePage.getNumber(),
                responsePage.getSize(),
                responsePage.getNumberOfElements(),
                responsePage.getTotalPages(),
                responsePage.isFirst(),
                responsePage.isLast(),
                responsePage.hasNext(),
                responsePage.hasPrevious());

    }
}
