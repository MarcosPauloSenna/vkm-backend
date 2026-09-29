package com.vkm_backend.group.infra.persistence.web.dto;

public record MemberResponse(MembershipResponse member,
                             String message) {
}
