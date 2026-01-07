package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.UserModel;

public interface IUserExternalPort {
    // Este  metodo es pa verificar el rol del usuario en el otro microservicio
    boolean isOwnerUser(Long userId);

    UserModel getUserById(Long userId);
}