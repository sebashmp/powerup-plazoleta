package com.pragma.powerup.domain.util;

public final class OrderMessages {

    private OrderMessages() {}

    // Save order
    public static final String ACTIVE_ORDER_IN_PROGRESS = "You already have an active order in progress.";
    public static final String RESTAURANT_NOT_FOUND = "The specified restaurant does not exist.";
    public static final String DISH_NOT_FROM_RESTAURANT = "One or more dishes do not belong to the selected restaurant.";

    // Authorization / Employees
    public static final String ONLY_EMPLOYEES_ACCESS = "Only employees can access this service.";
    public static final String ONLY_EMPLOYEES_ASSIGN = "Only employees can assign themselves to orders.";
    public static final String ONLY_EMPLOYEES_MARK_READY = "Only employees can mark orders as ready.";
    public static final String ONLY_EMPLOYEES_DELIVER = "Only employees can deliver orders.";
    public static final String EMPLOYEE_NOT_ASSIGNED = "The employee is not assigned to any restaurant.";
    public static final String ORDER_NOT_FOUND = "Order not found.";
    public static final String ORDER_DOES_NOT_EXIST = "The order does not exist.";
    public static final String ONLY_PENDING_ASSIGN = "Only pending orders can be assigned.";
    public static final String ORDER_DIFFERENT_RESTAURANT = "You can only assign yourself to orders from your own restaurant.";
    public static final String ORDER_NOT_IN_PREPARATION = "The order is not in preparation.";
    public static final String ONLY_CHEF_CAN_MARK = "You can only mark as ready orders assigned to you.";
    public static final String ORDER_NOT_READY_FOR_DELIVERY = "The order is not ready for delivery.";
    public static final String INVALID_SECURITY_PIN = "Invalid security PIN. Delivery rejected.";

    // Cancel
    public static final String ONLY_CLIENTS_CANCEL = "Only clients can cancel orders.";
    public static final String ONLY_OWNERS_CANCEL = "You can only cancel your own orders.";
    public static final String CANNOT_CANCEL_IN_PREPARATION = "Lo sentimos, tu pedido ya está en preparación y no puede cancelarse";

    // Notifications
    public static final String READY_MESSAGE_PREFIX = "Tu pedido está listo! Reclámalo con el PIN: ";
}
