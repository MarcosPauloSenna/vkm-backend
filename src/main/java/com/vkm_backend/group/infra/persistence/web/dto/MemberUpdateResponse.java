package com.vkm_backend.group.infra.persistence.web.dto;

public record MemberUpdateResponse(MemberStatusResponse member,
                                   String message) {
}
