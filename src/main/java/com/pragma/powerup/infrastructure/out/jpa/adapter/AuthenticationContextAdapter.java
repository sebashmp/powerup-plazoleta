package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.spi.IAuthenticationContextPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationContextAdapter implements IAuthenticationContextPort {

    @Override
    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            // El principal lo guardamos como el Email, pero el ID lo guardamos en las credenciales
            return (Long) authentication.getCredentials();
        }
        return null;
    }

    @Override
    public String getSelectedUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}