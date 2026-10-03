package com.vkm_backend.group.service;

import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.user.infra.persistence.entities.UserEntity;

/**
 * Porta para registrar vinculos (group_members) em nome de outros fluxos,
 * sem expor as regras de solicitacao/reaplicacao de membros.
 */
public interface MembershipRegistrar {

    /**
     * Registra o usuario como OWNER ja APPROVED do grupo recem-criado.
     */
    GroupMembersEntity registerOwner(GroupsEntity group, UserEntity owner);
}