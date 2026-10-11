package com.vkm_backend.tournaments.domain;

import com.vkm_backend.group.domain.Groups;
import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
public class Tournaments {

    private Long id;
    private String name;
    private GroupsEntity groupId;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private TournamentsStatus status;
    private TourrnamentsFormat format;
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    public Tournaments(Long createdBy,
                       Instant createdAt,
                       String description,
                       LocalDate endDate,
                       TourrnamentsFormat format,
                       GroupsEntity groupId,
                       Long id,
                       String name,
                       LocalDate startDate,
                       TournamentsStatus status,
                       Instant updatedAt,
                       Long updatedBy) {
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.description = description;
        this.endDate = endDate;
        this.format = format;
        this.groupId = groupId;
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.status = status;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public Tournaments() {
    }
}
