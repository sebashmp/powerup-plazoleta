package com.pragma.powerup.domain.spi;

public interface IAuthenticationContextPort {
    // El dominio pregunta por el correo o ID del usuario autenticado
    String getSelectedUserEmail();

    // Opcionalmente, obtener el ID directamente
    Long getAuthenticatedUserId();
}