package com.vkm_backend.tournaments.infra.specification;

import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import com.vkm_backend.tournaments.infra.entities.TournamentsTeamsEntity;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public final class TournamentTeamSpecification {

    public static Specification<TournamentsTeamsEntity> hasTeamsId(String teamsId) {
        if (teamsId == null || teamsId.isEmpty()) {
            return(root, query, cb) -> cb.conjunction();
        }

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();


                Subquery<Long> subquery = query.subquery(Long.class);

                Root<TeamsEntity> teams = subquery.from(TeamsEntity.class);

                subquery.select(teams.get("teamsId"))
                        .where(criteriaBuilder.like(teams.get("teamName"), teamsId));
                predicates.add(criteriaBuilder.exists(subquery));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
