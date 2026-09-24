package com.vkm_backend.group.infra.persistence.web.dto;

import org.springframework.data.domain.Page;

public record GroupsSearchResponse(Page<GroupResponse> groups,
                                   String message) {
}
