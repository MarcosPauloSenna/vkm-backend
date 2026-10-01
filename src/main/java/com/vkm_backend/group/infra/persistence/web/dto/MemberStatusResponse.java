package com.vkm_backend.group.infra.persistence.web.dto;

public record MemberStatusResponse(MemberFindResponse member,
                                   String message) {
}
