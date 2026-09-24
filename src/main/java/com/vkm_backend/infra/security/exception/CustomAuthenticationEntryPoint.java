package com.vkm_backend.infra.security.exception;

import com.vkm_backend.infra.global.handler.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Component
public class CustomAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private  final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        ErrorResponse error = (ErrorResponse) request.getAttribute("AUTH_ERROR") ;

        if(error == null){
            error = new ErrorResponse(LocalDateTime.now(ZONE),
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Usuário não autenticado.",
                    "AUTHENTICATION_EXCEPTION");
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
