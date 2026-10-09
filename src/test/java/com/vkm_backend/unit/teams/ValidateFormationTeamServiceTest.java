package com.vkm_backend.unit.teams;

import com.vkm_backend.group.domain.GroupMemberStatus;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.group.infra.persistence.repository.GroupMembersRepository;
import com.vkm_backend.group.infra.persistence.repository.GroupsRepository;
import com.vkm_backend.infra.global.exceptions.GroupNotFoundException;
import com.vkm_backend.infra.global.exceptions.ValidationException;
import com.vkm_backend.teams.service.ValidateFormationTeamService;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateFormationTeamServiceTest {

    private static final long GROUP_ID = 15L;
    private static final long MEMBER_ID = 42L;

    @Mock
    private GroupsRepository groupsRepository;
    @Mock
    private GroupMembersRepository groupMembersRepository;

    private ValidateFormationTeamService service;
    private GroupsEntity group;
    private GroupMembersEntity approvedMember;

    @BeforeEach
    void setUp() {
        service = new ValidateFormationTeamService(groupsRepository, groupMembersRepository);
        group = new GroupsEntity();
        group.setId(GROUP_ID);
        group.setName("Volley");
        approvedMember = new GroupMembersEntity();
        approvedMember.setId(MEMBER_ID);
        approvedMember.setGroupId(group);
        approvedMember.setStatus(GroupMemberStatus.APPROVED);
        approvedMember.setUserId(new UserEntity());
    }

    @Test
    void shouldReturnGroupAndApprovedMembersBelongingToIt() {
        when(groupsRepository.findById(GROUP_ID)).thenReturn(Optional.of(group));
        when(groupMembersRepository.findByIdAndGroupId_Id(MEMBER_ID, GROUP_ID))
                .thenReturn(Optional.of(approvedMember));

        var result = service.validateMemberInGroupId(List.of(MEMBER_ID), GROUP_ID);

        assertThat(result.group()).isSameAs(group);
        assertThat(result.teamMembers()).containsExactly(approvedMember);
    }

    @Test
    void shouldRejectUnknownGroup() {
        when(groupsRepository.findById(GROUP_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.validateMemberInGroupId(List.of(MEMBER_ID), GROUP_ID))
                .isInstanceOf(GroupNotFoundException.class);

        verifyNoInteractions(groupMembersRepository);
    }

    @Test
    void shouldRejectMemberOutsideGroup() {
        when(groupsRepository.findById(GROUP_ID)).thenReturn(Optional.of(group));
        when(groupMembersRepository.findByIdAndGroupId_Id(MEMBER_ID, GROUP_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.validateMemberInGroupId(List.of(MEMBER_ID), GROUP_ID))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("não pertence ao grupo");
    }

    @Test
    void shouldRejectMemberThatIsNotApproved() {
        approvedMember.setStatus(GroupMemberStatus.PENDING);
        when(groupsRepository.findById(GROUP_ID)).thenReturn(Optional.of(group));
        when(groupMembersRepository.findByIdAndGroupId_Id(MEMBER_ID, GROUP_ID))
                .thenReturn(Optional.of(approvedMember));

        assertThatThrownBy(() -> service.validateMemberInGroupId(List.of(MEMBER_ID), GROUP_ID))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("não estão aprovados");
    }

    @Test
    void shouldRejectDuplicateMemberIdsBeforeLookingUpTheDuplicate() {
        when(groupsRepository.findById(GROUP_ID)).thenReturn(Optional.of(group));
        when(groupMembersRepository.findByIdAndGroupId_Id(MEMBER_ID, GROUP_ID))
                .thenReturn(Optional.of(approvedMember));

        assertThatThrownBy(() -> service.validateMemberInGroupId(List.of(MEMBER_ID, MEMBER_ID), GROUP_ID))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("mais de uma vez");

        verify(groupMembersRepository).findByIdAndGroupId_Id(MEMBER_ID, GROUP_ID);
    }
}
