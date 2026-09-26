package com.vkm_backend.group.infra.persistence.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/group/{grouId}/members")
@Tag(name = "Membros", description = "Cadastro, consulta, aprovação e manutenção de membros")
public class GroupMemberController {
}
