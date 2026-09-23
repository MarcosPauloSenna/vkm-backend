package com.vkm_backend.infra.audit;

import com.vkm_backend.user.infra.persistence.UserEntity;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String){
            return Optional.empty();
        }


       UserEntity user = (UserEntity) authentication.getPrincipal();


        return Optional.of(user.getId());
    }
}
