package com.vkm_backend.group.infra.persistence.web.dto;

public record GroupSearchRequest(Long id,
                                 String name,
                                 String description,
                                 String city,
                                 String state) {
}
