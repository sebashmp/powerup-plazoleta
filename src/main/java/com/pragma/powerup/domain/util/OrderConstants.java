package com.pragma.powerup.domain.util;

import com.pragma.powerup.domain.model.OrderStatus;

import java.util.List;

public final class OrderConstants {

    private OrderConstants() {}

    // Roles
    public static final String ROLE_EMPLEADO = "ROLE_EMPLEADO";
    public static final String ROLE_CLIENTE = "ROLE_CLIENTE";

    // PIN
    public static final int PIN_LENGTH = 6;

    // Estados activos que bloquean crear nuevo pedido para el cliente
    public static final List<OrderStatus> ACTIVE_STATUSES = List.of(
            OrderStatus.PENDIENTE,
            OrderStatus.EN_PREPARACION,
            OrderStatus.LISTO
    );
}
