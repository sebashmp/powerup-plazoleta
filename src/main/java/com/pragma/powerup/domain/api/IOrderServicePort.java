package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;

import java.util.List;

public interface IOrderServicePort {
    void saveOrder(OrderModel orderModel);
    List<OrderModel> getOrdersByStatus(OrderStatus status, Integer page, Integer size);
}