package com.pragma.powerup.domain.usecase.factory;

import com.pragma.powerup.domain.model.OrderStatus;

public final class OrderTestConstants {

    private OrderTestConstants() {}

    // IDS
    public static final Long CLIENT_ID = 10L;
    public static final Long EMPLOYEE_ID = 20L;
    public static final Long RESTAURANT_ID = 1L;
    public static final Long OTHER_RESTAURANT_ID = 2L;
    public static final Long DISH_ID = 100L;

    // PAGINATION
    public static final Integer PAGE = 0;
    public static final Integer SIZE = 10;

    // ROLES
    public static final String ROLE_CLIENTE = "ROLE_CLIENTE";
    public static final String ROLE_EMPLEADO = "ROLE_EMPLEADO";

    // STATUS
    public static final OrderStatus DEFAULT_STATUS = OrderStatus.PENDIENTE;

    public static final int PIN_LENGTH = 6;
}
