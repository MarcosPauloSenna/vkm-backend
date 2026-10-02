package com.vkm_backend.group.service;

import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;

/**
 * Política de acesso de um usuário a um grupo. Os use cases dependem apenas deste contrato;
 * a regra concreta (quem pode o quê) fica na implementação.
 * Todos os métodos exigem vínculo APPROVED e devolvem o vínculo do usuário autenticado.
 */
public interface GroupAccessPolicy {

    /**
     * Qualquer membro APPROVED do grupo.
     *
     * @throws com.vkm_backend.infra.global.exceptions.GroupNotFoundException grupo inexistente
     * @throws com.vkm_backend.infra.global.exceptions.MemberNotFoundException usuário sem vínculo com o grupo
     * @throws com.vkm_backend.infra.global.exceptions.ValidationException vínculo não APPROVED
     */
    GroupMembersEntity authorize(Long groupId, String username);

    /**
     * Membro APPROVED com role ADMIN ou OWNER.
     *
     * @throws org.springframework.security.access.AccessDeniedException role insuficiente
     */
    GroupMembersEntity authorizeAdminAndOwner(Long groupId, String username);

    /**
     * Membro APPROVED com role OWNER.
     *
     * @throws org.springframework.security.access.AccessDeniedException role insuficiente
     */
    GroupMembersEntity authorizeOwner(Long groupId, String username);
}