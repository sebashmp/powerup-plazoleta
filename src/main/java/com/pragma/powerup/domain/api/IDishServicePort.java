package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.DishModel;

public interface IDishServicePort {
    void saveDish(DishModel dishModel);
    void updateDish(Long id, DishModel dishModel);
    void changeDishStatus(Long dishId, Boolean active);
}