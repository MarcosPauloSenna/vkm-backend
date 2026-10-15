package com.vkm_backend.tournaments.infra.specification;

import com.vkm_backend.tournaments.infra.entities.TournamentsTeamsEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public final class TournamentTeamSpecification {

    public static Specification<TournamentsTeamsEntity> hasTeamsId(String teamsId) {
        if (teamsId == null || teamsId.isBlank()) {
            return(root, query, cb) -> cb.conjunction();
        }

        String pattern = "%" + teamsId.trim().toLowerCase() + "%";
        return (root, query, criteriaBuilder) -> {
            Predicate nameMatches = criteriaBuilder.like(
                    criteriaBuilder.lower(root.join("teamId").get("name")),
                    pattern);
            return criteriaBuilder.and(nameMatches);
        };
    }
}
