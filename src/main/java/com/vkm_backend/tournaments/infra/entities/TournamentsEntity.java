package com.vkm_backend.tournaments.infra.entities;

import com.vkm_backend.group.infra.persistence.entities.GroupsEntity;
import com.vkm_backend.infra.audit.Auditable;
import com.vkm_backend.tournaments.domain.TournamentsStatus;
import com.vkm_backend.tournaments.domain.TourrnamentsFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Table(name = "tournaments")
@Entity
public class TournamentsEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_tournaments_groups"))
    private GroupsEntity groupId;

    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TournamentsStatus status;

    @Column(name = "format", nullable = false)
    @Enumerated(EnumType.STRING)
    private TourrnamentsFormat format;

    public TournamentsEntity(String description,
                             LocalDate endDate,
                             TourrnamentsFormat format,
                             GroupsEntity groupId,
                             Long id,
                             String name,
                             LocalDate startDate,
                             TournamentsStatus status) {
        this.description = description;
        this.endDate = endDate;
        this.format = format;
        this.groupId = groupId;
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.status = status;
    }

    public TournamentsEntity() {
    }
}
