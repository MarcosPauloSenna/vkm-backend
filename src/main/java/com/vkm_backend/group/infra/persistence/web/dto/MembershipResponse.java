package com.vkm_backend.group.infra.persistence.web.dto;

public record MembershipResponse(Long id,
                                 String groupName,
                                 String username,
                                 String role,
                                 String satus) {
}
