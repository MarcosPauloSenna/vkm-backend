package com.vkm_backend.attendance.infra.persistence.entities;

import com.vkm_backend.attendance.domain.AttendanceStatus;
import com.vkm_backend.game.infra.persistence.entities.GameDaysEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import com.vkm_backend.infra.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "attendance")
public class AttendanceEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_day_id", nullable = false)
    private GameDaysEntity gameDayId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_member_id", nullable = false)
    private GroupMembersEntity groupMemberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status;

    @Column(name = "check_in_at")
    private Instant checkInAt;

    @Column(name = "notes")
    private String notes;

    public AttendanceEntity(Instant checkInAt,
                            GameDaysEntity gameDayId,
                            GroupMembersEntity groupMemberId,
                            Long id,
                            String notes,
                            AttendanceStatus status) {
        this.checkInAt = checkInAt;
        this.gameDayId = gameDayId;
        this.groupMemberId = groupMemberId;
        this.id = id;
        this.notes = notes;
        this.status = status;
    }

    public AttendanceEntity() {
    }
}
