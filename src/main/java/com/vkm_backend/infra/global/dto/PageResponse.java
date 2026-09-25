package com.vkm_backend.infra.global.dto;

import java.util.List;

public record git <T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
