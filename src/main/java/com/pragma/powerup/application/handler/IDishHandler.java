package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.DishStatusRequestDto;
import com.pragma.powerup.application.dto.request.DishUpdateDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.dto.response.PageResponse;

import java.util.List;

public interface IDishHandler {
    void saveDish(DishRequestDto dishRequestDto);
    void updateDish(Long id, DishUpdateDto dishUpdateDto);
    void changeDishStatus(Long dishId, DishStatusRequestDto dishStatusRequestDto);

    PageResponse<DishResponseDto> getDishesByRestaurant(Long restaurantId, Long categoryId, Integer page, Integer size);
}