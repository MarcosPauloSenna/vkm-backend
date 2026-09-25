package com.vkm_backend.group.infra.persistence.web.dto;

import com.vkm_backend.infra.global.dto.PageResponse;
import org.springframework.data.domain.Page;

public record GroupsSearchResponse(PageResponse<GroupResponse> groups,
                                   String message) {
}
