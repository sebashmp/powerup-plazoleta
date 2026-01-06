package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.GenericPage;

import java.util.List;

public interface IDishServicePort {
    void saveDish(DishModel dishModel);
    void updateDish(Long id, DishModel dishModel);
    void changeDishStatus(Long dishId, Boolean active);
    GenericPage<DishModel> getDishesByRestaurant(Long restaurantId, Long categoryId, Integer page, Integer size);
}