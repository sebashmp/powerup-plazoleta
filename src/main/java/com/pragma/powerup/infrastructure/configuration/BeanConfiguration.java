package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.IDishServicePort;
import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.spi.IAuthenticationContextPort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserExternalPort;
import com.pragma.powerup.domain.usecase.DishUseCase;
import com.pragma.powerup.domain.usecase.RestaurantUseCase;
import com.pragma.powerup.infrastructure.out.feign.IUserFeignClient;
import com.pragma.powerup.infrastructure.out.feign.adapter.UserExternalAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.AuthenticationContextAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.DishJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IDishRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    private final IUserFeignClient userFeignClient;

    @Bean
    public IAuthenticationContextPort authContextPort() {
        return new AuthenticationContextAdapter();
    }

    @Bean
    public IDishPersistencePort dishPersistencePort() {
        return new DishJpaAdapter(dishRepository, dishEntityMapper);
    }

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public IUserExternalPort userExternalPort() {
        return new UserExternalAdapter(userFeignClient);
    }


    @Bean
    public IDishServicePort dishServicePort() {
        return new DishUseCase(dishPersistencePort(), restaurantPersistencePort(), authContextPort());
    }

    @Bean
    public IRestaurantServicePort restaurantServicePort() {
        return new RestaurantUseCase(restaurantPersistencePort(), userExternalPort());
    }
}