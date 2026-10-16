package com.vkm_backend.game.domain;

import com.vkm_backend.attendance.infra.persistence.entities.AttendanceEntity;
import com.vkm_backend.game.infra.persistence.entities.GameDaysEntity;
import com.vkm_backend.game.infra.persistence.entities.GameSessionsEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameSessionMembers {

    private Long id;
    private GameDaysEntity gameDayId;
    private GameSessionsEntity gameSessionId;
    private AttendanceEntity attendanceId;
    private GameSessionMemberStatus status;
    private Instant joinedAt;
    private Instant leftAt;
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
