package com.vkm_backend.user.usecase;

import com.vkm_backend.group.usecase.SortWhitelist;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.mapper.PageResponseMapper;
import com.vkm_backend.infra.global.specification.DynamicFilter;
import com.vkm_backend.infra.global.specification.DynamicSpecification;
import com.vkm_backend.user.infra.mapper.UserMapper;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import com.vkm_backend.user.infra.persistence.repository.UserRepository;
import com.vkm_backend.user.infra.web.dto.FindUserAdmRequest;
import com.vkm_backend.user.infra.web.dto.UserResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FindUsersUseCase {

    private final UserRepository usersRepository;
    private final PageResponseMapper pageResponseMapper;
    private final UserMapper userMapper;

    public FindUsersUseCase(UserRepository usersRepository, PageResponseMapper pageResponseMapper, UserMapper userMapper) {
        this.usersRepository = usersRepository;
        this.pageResponseMapper = pageResponseMapper;
        this.userMapper = userMapper;
    }

    @Transactional
    public PageResponse<UserResponse> execute(FindUserAdmRequest request, Pageable pageable) {
        pageable = SortWhitelist.validate(
                pageable,
                UserSortFields.SORT_FIELDS
        );

        Page<UserEntity> usersEntityList = usersRepository.findAll(DynamicSpecification.<UserEntity>where(DynamicFilter.toEquals(request.id(), "id"))
                .and(DynamicFilter.toLike(request.name(), "name"))
                .and(DynamicFilter.toLike(request.username(), "username"))
                .and(DynamicFilter.toLike(request.birthDate(), "birthDate"))
                .and(DynamicFilter.toLike(request.active(), "active")),pageable);

        return pageResponseMapper.toPageResponse(usersEntityList, userMapper);
    }
}
