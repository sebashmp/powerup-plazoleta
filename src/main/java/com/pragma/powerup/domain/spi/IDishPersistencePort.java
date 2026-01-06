package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.GenericPage;

public interface IDishPersistencePort {
    void saveDish(DishModel dishModel);
    DishModel findById(Long id);
    void updateDish(DishModel dishModel);
    GenericPage<DishModel> getDishesByRestaurant(Long restaurantId, Long categoryId, Integer page, Integer size);
}