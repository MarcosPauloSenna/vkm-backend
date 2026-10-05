package com.vkm_backend.group.infra.persistence.web.dto;


import java.time.Instant;

public record MemberStatusResponse(Long id,
                                   String name,
                                   String role,
                                   String status,
                                   Instant startGroup) {
}
