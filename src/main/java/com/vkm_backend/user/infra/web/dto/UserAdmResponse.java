package com.vkm_backend.user.infra.web.dto;

import com.vkm_backend.infra.global.dto.PageResponse;

public record UserAdmResponse(PageResponse<UserResponse> user, String message) {
}
