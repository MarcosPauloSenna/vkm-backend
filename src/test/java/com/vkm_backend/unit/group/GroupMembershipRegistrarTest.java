package com.vkm_backend.unit.group;

import com.vkm_backend.group.domain.GroupMemberRole;
import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.service.GroupMembershipRegistrar;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupMembershipRegistrarTest {

    @Mock
    private GroupMembersRepository groupMembersRepository;
    @InjectMocks
    private GroupMembershipRegistrar registrar;

    @Test
    void shouldRegisterOwnerAsApprovedOwner() {
        GroupsEntity group = new GroupsEntity();
        UserEntity owner = new UserEntity();
        when(groupMembersRepository.save(any(GroupMembersEntity.class))).thenAnswer(i -> i.getArgument(0));

        GroupMembersEntity result = registrar.registerOwner(group, owner);

        assertThat(result.getGroupId()).isSameAs(group);
        assertThat(result.getUserId()).isSameAs(owner);
        assertThat(result.getRole()).isEqualTo(GroupMemberRole.OWNER);
        assertThat(result.getStatus()).isEqualTo(GroupMemberStatus.APPROVED);
        assertThat(result.getApprovedAt()).isNotNull();
    }
}