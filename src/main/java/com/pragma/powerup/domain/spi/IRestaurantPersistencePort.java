package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.RestaurantModel;

public interface IRestaurantPersistencePort {
    void saveRestaurant(RestaurantModel restaurantModel);
}