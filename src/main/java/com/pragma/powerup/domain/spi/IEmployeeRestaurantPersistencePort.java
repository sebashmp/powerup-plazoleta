package com.pragma.powerup.domain.spi;

public interface IEmployeeRestaurantPersistencePort {
    void saveEmployeeRestaurant(Long employeeId, Long restaurantId);
    Long getRestaurantIdByEmployeeId(Long employeeId);
}