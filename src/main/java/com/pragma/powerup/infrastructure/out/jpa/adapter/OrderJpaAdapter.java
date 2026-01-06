package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {
    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    @Override
    public void saveOrder(OrderModel orderModel) {
        OrderEntity orderEntity = orderEntityMapper.toEntity(orderModel);

        // En relaciones bidireccionales de JPA, cada detalle debe conocer a su padre
        orderEntity.getOrderDishes().forEach(dish -> dish.setOrder(orderEntity));

        orderRepository.save(orderEntity);
    }

    @Override
    public boolean existsByClientIdAndStatusIn(Long clientId, List<OrderStatus> statuses) {
        // Convertimos la lista de Enums a Strings para el query de BD
        List<String> statusStrings = statuses.stream()
                .map(Enum::name)
                .collect(Collectors.toList());
        return orderRepository.existsByClientIdAndStatusIn(clientId, statusStrings);
    }

    @Override
    public List<OrderModel> getOrdersByStatusAndRestaurant(OrderStatus status, Long restaurantId, Integer page, Integer size) {
        return List.of();
    }
}