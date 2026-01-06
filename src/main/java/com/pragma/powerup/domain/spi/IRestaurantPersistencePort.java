package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.GenericPage;
import com.pragma.powerup.domain.model.RestaurantModel;

import java.util.List;

public interface IRestaurantPersistencePort {
    void saveRestaurant(RestaurantModel restaurantModel);
    RestaurantModel getRestaurantById(Long id);
    GenericPage<RestaurantModel> getAllRestaurants(Integer page, Integer size);
}