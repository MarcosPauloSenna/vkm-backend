package com.vkm_backend.group.infra.persistence.web.dto;

import com.vkm_backend.infra.global.dto.PageResponse;

public record GroupsSearchResponse(PageResponse<GroupResponse> groups,
                                   String message) {
}
