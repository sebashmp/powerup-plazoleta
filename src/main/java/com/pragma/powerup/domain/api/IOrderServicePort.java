package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.GenericPage;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;

public interface IOrderServicePort {
    void saveOrder(OrderModel orderModel);
    GenericPage<OrderModel> getOrdersByStatus(OrderStatus status, Integer page, Integer size);
    void assignOrder(Long orderId);
}