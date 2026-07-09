package com.delivery.global.config;

import com.delivery.global.security.config.CustomUserDetails;
import java.util.Optional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class CustomAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("system");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return Optional.of(userDetails.getId() + "_" + userDetails.getUsername());
    }
}
