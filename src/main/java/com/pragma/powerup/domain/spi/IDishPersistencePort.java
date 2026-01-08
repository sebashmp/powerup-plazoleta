package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.GenericPage;

public interface IDishPersistencePort {
    void upsert(DishModel dishModel);
    DishModel findById(Long id);
    GenericPage<DishModel> getDishesByRestaurant(Long restaurantId, Long categoryId, Integer page, Integer size);
}