package com.vkm_backend.group.infra.persistence.web.dto;

public record GroupResponse(Long id,
                            String name,
                            String description,
                            String city,
                            String state,
                            String active) {

}
