package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.domain.spi.IUserExternalPort;
import com.pragma.powerup.infrastructure.out.feign.IUserFeignClient;
import com.pragma.powerup.infrastructure.out.feign.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserExternalAdapter implements IUserExternalPort {

    private final IUserFeignClient userFeignClient;

    @Override
    public boolean isOwnerUser(Long userId) {
        try {
            UserResponseDto user = userFeignClient.getUserById(userId);
            return user != null && user.getRole().getId().equals(2L);
        } catch (feign.FeignException.NotFound e) {
            // Si el usuario no existe en el otro microservicio, retornamos false para que el UseCase lance la DomainException personalizada.
            return false;
        } catch (Exception e) {
            // Cualquier otro error de conexión también se maneja como false
            return false;
        }
    }
}