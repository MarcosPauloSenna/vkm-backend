package com.vkm_backend.teams.infra.persistence.repository;

import com.vkm_backend.infra.global.specification.DynamicRepository;
import com.vkm_backend.teams.infra.persistence.entities.TeamMembersEntity;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TeamMembersRepository extends DynamicRepository<TeamMembersEntity, Long> {

    Collection<TeamMembersEntity> findByTeamsId(TeamsEntity teamsId);

    List<TeamMembersEntity> findByTeamsId_Id(Long teamsIdId);
}
