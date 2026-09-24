package com.vkm_backend.infra.security.exception;

import tools.jackson.databind.ObjectMapper;
import com.vkm_backend.infra.global.handler.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class CustomAccessDeniedHandler
        implements AccessDeniedHandler {
    private  final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        var error = new ErrorResponse(LocalDateTime.now(ZONE),
                HttpServletResponse.SC_FORBIDDEN,   "Usuário não autorizado.",
                "ACCESS_DENIED");
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
