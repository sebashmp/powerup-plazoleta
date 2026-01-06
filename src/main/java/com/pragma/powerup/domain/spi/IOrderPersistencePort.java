package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import java.util.List;

public interface IOrderPersistencePort {
    void saveOrder(OrderModel orderModel);

    // Para validar que el cliente no tenga pedidos activos
    boolean existsByClientIdAndStatusIn(Long clientId, List<OrderStatus> statuses);

    List<OrderModel> getOrdersByStatusAndRestaurant(OrderStatus status, Long restaurantId, Integer page, Integer size);
}