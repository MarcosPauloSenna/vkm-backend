package com.vkm_backend.teams.infra.specification;

import com.vkm_backend.teams.domain.MemberFilterMode;
import com.vkm_backend.teams.infra.persistence.entities.TeamMembersEntity;
import com.vkm_backend.teams.infra.persistence.entities.TeamsEntity;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public final class TeamSpecification {

    public static Specification<TeamsEntity> hasMemberId(List<Long> membersId,
                                                         MemberFilterMode mode) {
        if (membersId == null || membersId.isEmpty()) {
            return(root, query, cb) -> cb.conjunction();
        }

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (Long memberId : membersId) {

                Subquery<Long> subquery = query.subquery(Long.class);

                Root<TeamMembersEntity> teamMember = subquery.from(TeamMembersEntity.class);

                subquery.select(teamMember.get("teamsId"))
                        .where(criteriaBuilder.equal(teamMember.get("teamsId"), root),
                                criteriaBuilder.equal(teamMember.get("groupMembersId").get("id"), memberId));
                predicates.add(criteriaBuilder.exists(subquery));
            }
            return mode == MemberFilterMode.ALL
                    ? criteriaBuilder.and(predicates.toArray(new Predicate[0]))
                    : criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}
