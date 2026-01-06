package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.OrderDishRequestDto;
import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.domain.model.OrderDishModel;
import com.pragma.powerup.domain.model.OrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderRequestMapper {
    @Mapping(target = "restaurant.id", source = "restaurantId")
    @Mapping(target = "orderDishes", source = "dishes")
    OrderModel toModel(OrderRequestDto orderRequestDto);

    @Mapping(target = "dish.id", source = "dishId")
    OrderDishModel toModel(OrderDishRequestDto orderDishRequestDto);
}