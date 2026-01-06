package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.OrderDishModel;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderDishEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderEntityMapper {

    @Mapping(target = "restaurant.id", source = "restaurant.id")
    @Mapping(target = "orderDishes", source = "orderDishes")
    OrderEntity toEntity(OrderModel orderModel);

    @Mapping(target = "dish.id", source = "dish.id")
    @Mapping(target = "order", ignore = true) // Se va a asignar manualmente en el Adaptador
    OrderDishEntity toEntity(OrderDishModel orderDishModel);

    @Mapping(target = "restaurant.id", source = "restaurant.id")
    OrderModel toModel(OrderEntity orderEntity);

    List<OrderModel> toModelList(List<OrderEntity> orderEntityList);
}