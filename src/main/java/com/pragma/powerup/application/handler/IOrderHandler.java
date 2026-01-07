package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.OrderDeliveryRequestDto;
import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.dto.response.PageResponse;
import com.pragma.powerup.domain.model.OrderStatus;

import java.util.List;

public interface IOrderHandler {
    void saveOrder(OrderRequestDto orderRequestDto);

    PageResponse<OrderResponseDto> getOrdersByStatus(OrderStatus status, Integer page, Integer size);

    void assignOrder(Long orderId);


    void markOrderAsReady(Long id);

    void deliverOrder(Long orderId, OrderDeliveryRequestDto requestDto);

    void cancelOrder(Long orderId);
}
