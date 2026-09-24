package com.vkm_backend.infra.security;


import com.vkm_backend.infra.security.exception.TokenValidationResult;
import com.vkm_backend.user.infra.persistence.UserRepository;
import com.vkm_backend.user.service.AccessTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {


    public final AccessTokenService accessTokenService;


    public final UserRepository userRepository;
    private AuthenticationEntryPoint authenticationEntryPoint;

    public SecurityFilter(AccessTokenService accessTokenService, UserRepository userRepository) {
        this.accessTokenService = accessTokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if (token != null){
            var result = accessTokenService.validationAccessToken(token);
            if (result.status() != TokenValidationResult.Status.INVALID){
                request.setAttribute("AUTH_ERROR", result.errorResponse());
            }

            var username = result.username();
            UserDetails user = userRepository.findByUsername(username);
            if (user == null || !user.isEnabled()) {
                filterChain.doFilter(request, response);
                return;
            }
            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {return null;}
        return authHeader.substring(7);
    }
}
