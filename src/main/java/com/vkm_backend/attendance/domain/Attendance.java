package com.vkm_backend.attendance.domain;

import com.vkm_backend.game.infra.persistence.entities.GameDaysEntity;
import com.vkm_backend.group.infra.persistence.entities.GroupMembersEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    private Long id;
    private GameDaysEntity gameDayId;
    private GroupMembersEntity groupMemberId;
    private AttendanceStatus status;
    private Instant checkInAt;
    private String notes;
    private Long createdBy;
    private Long updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
