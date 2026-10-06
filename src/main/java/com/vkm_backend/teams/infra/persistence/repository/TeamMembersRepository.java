package com.vkm_backend.teams.infra.persistence.repository;

import com.vkm_backend.infra.global.specification.DynamicRepository;
import com.vkm_backend.teams.infra.persistence.entities.TeamMembersEntity;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;

import java.util.Collection;

public interface TeamMembersRepository extends DynamicRepository<TeamMembersEntity, Long> {

    Collection<TeamMembersEntity> findByTeamsId(TeamsEntity teamsId);
}
