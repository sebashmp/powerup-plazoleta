package com.pragma.powerup.domain.spi;

public interface IUserExternalPort {
    // Este  metodo es pa verificar el rol del usuario en el otro microservicio
    boolean isOwnerUser(Long userId);
}