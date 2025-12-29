package com.pragma.powerup.infrastructure.out.feign;

import com.pragma.powerup.infrastructure.out.feign.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// El nombre debe coincidir con el nombre de la app de usuarios o usar la URL
@FeignClient(name = "user-service", url = "localhost:8081/users")
public interface IUserFeignClient {

    @GetMapping("/{id}")
    UserResponseDto getUserById(@PathVariable("id") Long id);
}