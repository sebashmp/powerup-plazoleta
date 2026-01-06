package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.DishModel;

import java.util.List;

public interface IDishPersistencePort {
    void saveDish(DishModel dishModel);
    DishModel findById(Long id);
    void updateDish(DishModel dishModel);
    List<DishModel> getDishesByRestaurant(Long restaurantId, Long categoryId, Integer page, Integer size);
}