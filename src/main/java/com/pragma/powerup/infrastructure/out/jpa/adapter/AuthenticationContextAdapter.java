package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.spi.IAuthenticationContextPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationContextAdapter implements IAuthenticationContextPort {

    @Override
    public String getSelectedUserEmail() {
        // Extrae el email del contexto de seguridad de Spring
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @Override
    public Long getAuthenticatedUserId() {
        return 1L; // TODO: Implementar extracción real del ID desde el Token
    }
}