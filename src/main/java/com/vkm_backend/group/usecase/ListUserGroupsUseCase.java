package com.vkm_backend.group.usecase;

import com.vkm_backend.group.infra.persistence.web.dto.GroupResponse;
import com.vkm_backend.group.infra.persistence.web.dto.GroupSearchRequest;
import com.vkm_backend.group.service.ListGroupsUserservice;
import com.vkm_backend.infra.global.dto.PageResponse;
import com.vkm_backend.infra.global.exceptions.ValidationException;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import com.vkm_backend.user.infra.persistence.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ListUserGroupsUseCase {

    private final UserRepository userRepository;
    private final ListGroupsUserservice listGroupsUserservice ;

    public ListUserGroupsUseCase(UserRepository userRepository, ListGroupsUserservice listGroupsUserservice) {
        this.userRepository = userRepository;
        this.listGroupsUserservice = listGroupsUserservice;
    }


    @Transactional
    public PageResponse<GroupResponse> execute(GroupSearchRequest request, Long userId, Pageable pageable) {
        pageable = SortWhitelist.validate(
                pageable,
                GroupSortFields.SORT_FIELDS
        );

        Optional<UserEntity> userFind = userRepository.findById(userId);

        if (userFind.isEmpty()) {
            throw new ValidationException("Usuario não localizado");
        }

        UserEntity user = userFind.get();

        return listGroupsUserservice.getGroupUserService(request, pageable, user);
    }

}
