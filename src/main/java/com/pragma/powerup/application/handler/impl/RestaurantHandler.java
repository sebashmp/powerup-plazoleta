package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.PageResponse;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.handler.IRestaurantHandler;
import com.pragma.powerup.application.mapper.IRestaurantRequestMapper;
import com.pragma.powerup.application.mapper.IRestaurantResponseMapper;
import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.model.GenericPage;
import com.pragma.powerup.domain.model.RestaurantModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantHandler implements IRestaurantHandler {

    private final IRestaurantServicePort restaurantServicePort;
    private final IRestaurantRequestMapper restaurantRequestMapper;
    private final IRestaurantResponseMapper restaurantResponseMapper;

    @Override
    public void saveRestaurant(RestaurantRequestDto restaurantRequestDto) {

        restaurantServicePort.saveRestaurant(restaurantRequestMapper.toModel(restaurantRequestDto));
    }

    @Override
    public PageResponse<RestaurantResponseDto> getRestaurants(Integer page, Integer size) {
        GenericPage<RestaurantModel> domainPage = restaurantServicePort.getRestaurants(page, size);

        List<RestaurantResponseDto> content = restaurantResponseMapper.toResponseList(domainPage.getContent());

        return new PageResponse<>(
                content,
                domainPage.getPageNumber(),
                domainPage.getPageSize(),
                domainPage.getTotalElements(),
                domainPage.getTotalPages(),
                domainPage.getFirst(),
                domainPage.getLast(),
                domainPage.getHasNext(),
                domainPage.getHasPrevious()
        );
    }
}