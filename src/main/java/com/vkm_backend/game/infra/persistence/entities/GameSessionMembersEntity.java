package com.vkm_backend.game.infra.persistence.entities;

import com.vkm_backend.attendance.infra.persistence.entities.AttendanceEntity;
import com.vkm_backend.game.domain.GameSessionMemberStatus;
import com.vkm_backend.infra.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "game_session_members")
public class GameSessionMembersEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_day_id", nullable = false)
    private GameDaysEntity gameDayId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_session_id", nullable = false)
    private GameSessionsEntity gameSessionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attendance_id", nullable = false)
    private AttendanceEntity attendanceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GameSessionMemberStatus status;

    @Column(name = "joined_at")
    private Instant joinedAt;
    @Column(name = "left_at")
    private Instant leftAt;
}
