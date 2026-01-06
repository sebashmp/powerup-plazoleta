package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.PageResponse;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;

import java.util.List;

public interface IRestaurantHandler {
    void saveRestaurant(RestaurantRequestDto restaurantRequestDto);

    PageResponse<RestaurantResponseDto> getRestaurants(Integer page, Integer size);
    void linkEmployee(Long restaurantId, Long employeeId);
}