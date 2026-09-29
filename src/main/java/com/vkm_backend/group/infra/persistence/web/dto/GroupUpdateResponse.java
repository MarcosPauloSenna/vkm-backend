package com.vkm_backend.group.infra.persistence.web.dto;

public record GroupUpdateResponse(GroupResponse group,
                                  String message) {
}
