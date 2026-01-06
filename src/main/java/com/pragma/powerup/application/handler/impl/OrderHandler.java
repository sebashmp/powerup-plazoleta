package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.handler.IOrderHandler;
import com.pragma.powerup.application.mapper.IOrderRequestMapper;
import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.model.OrderModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional // Garantiza la atomicidad de la operación
public class OrderHandler implements IOrderHandler {

    private final IOrderServicePort orderServicePort;
    private final IOrderRequestMapper orderRequestMapper;

    @Override
    public void saveOrder(OrderRequestDto orderRequestDto) {
        // 1. Convertimos el DTO (JSON plano) a Modelo de Dominio
        OrderModel orderModel = orderRequestMapper.toModel(orderRequestDto);

        // 2. Llamamos al puerto del servicio (UseCase) para aplicar reglas y persistir
        orderServicePort.saveOrder(orderModel);
    }
}