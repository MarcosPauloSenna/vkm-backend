package com.vkm_backend.group.infra.persistence.web.dto;

public record CreateGroupResponse(GroupResponse group,
                                  String message) {
}
