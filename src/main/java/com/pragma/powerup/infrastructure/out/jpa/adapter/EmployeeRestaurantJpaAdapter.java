package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.spi.IEmployeeRestaurantPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.RestaurantEmployeeEntity;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantEmployeeRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmployeeRestaurantJpaAdapter implements IEmployeeRestaurantPersistencePort {
    private final IRestaurantEmployeeRepository repository;

    @Override
    public void saveEmployeeRestaurant(Long employeeId, Long restaurantId) {
        RestaurantEmployeeEntity entity = new RestaurantEmployeeEntity(employeeId, restaurantId);
        repository.save(entity);
    }

    @Override
    public Long getRestaurantIdByEmployeeId(Long employeeId) {
        return repository.findByEmployeeId(employeeId)
                .map(RestaurantEmployeeEntity::getRestaurantId)
                .orElse(null);
    }
}