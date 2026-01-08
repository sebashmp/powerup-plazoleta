package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.util.OrderConstants;
import com.pragma.powerup.domain.util.OrderMessages;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.*;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final IAuthenticationContextPort authContextPort;
    private final IEmployeeRestaurantPersistencePort employeeRestaurantPersistencePort;
    private final IUserExternalPort userExternalPort;
    private final IMessagingExternalPort messagingExternalPort;
    private final ITraceExternalPort traceExternalPort;

    public OrderUseCase(IOrderPersistencePort orderPersistencePort,
                        IRestaurantPersistencePort restaurantPersistencePort,
                        IDishPersistencePort dishPersistencePort,
                        IAuthenticationContextPort authContextPort,
                        IEmployeeRestaurantPersistencePort employeeRestaurantPersistencePort,
                        IUserExternalPort userExternalPort,
                        IMessagingExternalPort messagingExternalPort,
                        ITraceExternalPort traceExternalPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.authContextPort = authContextPort;
        this.employeeRestaurantPersistencePort = employeeRestaurantPersistencePort;
        this.userExternalPort = userExternalPort;
        this.messagingExternalPort = messagingExternalPort;
        this.traceExternalPort = traceExternalPort;
    }

    @Override
    public void saveOrder(OrderModel orderModel) {
        Long clientId = authContextPort.getAuthenticatedUserId();
        orderModel.setClientId(clientId);

        if (orderPersistencePort.existsByClientIdAndStatusIn(clientId, OrderConstants.ACTIVE_STATUSES)) {
            throw new DomainException(OrderMessages.ACTIVE_ORDER_IN_PROGRESS);
        }

        ensureRestaurantExists(orderModel.getRestaurant().getId());

        orderModel.getOrderDishes().forEach(orderDish -> validateAndFillDish(orderDish, orderModel.getRestaurant().getId()));

        orderModel.setDate(LocalDate.now());
        orderModel.setStatus(OrderStatus.PENDIENTE);

        orderPersistencePort.saveOrder(orderModel);
    }

    private void ensureRestaurantExists(Long restaurantId) {
        if (restaurantPersistencePort.getRestaurantById(restaurantId) == null) {
            throw new DomainException(OrderMessages.RESTAURANT_NOT_FOUND);
        }
    }

    private void validateAndFillDish(OrderDishModel orderDish, Long restaurantId) {
        DishModel dish = dishPersistencePort.findById(orderDish.getDish().getId());
        if (dish == null || !dish.getRestaurant().getId().equals(restaurantId)) {
            throw new DomainException(OrderMessages.DISH_NOT_FROM_RESTAURANT);
        }
        orderDish.setDish(dish);
    }

    @Override
    public GenericPage<OrderModel> getOrdersByStatus(OrderStatus status, Integer page, Integer size) {

        requireRole(OrderConstants.ROLE_EMPLEADO, OrderMessages.ONLY_EMPLOYEES_ACCESS);

        Long employeeId = authContextPort.getAuthenticatedUserId();
        Long restaurantId = employeeRestaurantPersistencePort.getRestaurantIdByEmployeeId(employeeId);

        if (restaurantId == null) {
            throw new DomainException(OrderMessages.EMPLOYEE_NOT_ASSIGNED);
        }

        return orderPersistencePort.getOrdersByStatusAndRestaurant(status, restaurantId, page, size);
    }

    @Override
    public void assignOrder(Long orderId) {

        requireRole(OrderConstants.ROLE_EMPLEADO, OrderMessages.ONLY_EMPLOYEES_ASSIGN);

        Long employeeId = authContextPort.getAuthenticatedUserId();
        Long restaurantId = employeeRestaurantPersistencePort.getRestaurantIdByEmployeeId(employeeId);

        OrderModel order = fetchOrderOrThrow(orderId, OrderMessages.ORDER_DOES_NOT_EXIST);
        String oldStatus = order.getStatus().name();

        if (order.getStatus() != OrderStatus.PENDIENTE) {
            throw new DomainException(OrderMessages.ONLY_PENDING_ASSIGN);
        }

        if (!order.getRestaurant().getId().equals(restaurantId)) {
            throw new DomainException(OrderMessages.ORDER_DIFFERENT_RESTAURANT);
        }

        order.setChefId(employeeId);
        order.setStatus(OrderStatus.EN_PREPARACION);

        sendTrace(order, oldStatus, OrderStatus.EN_PREPARACION.name());

        orderPersistencePort.saveOrder(order);
    }

    @Override
    public void markOrderAsReady(Long orderId) {

        requireRole(OrderConstants.ROLE_EMPLEADO, OrderMessages.ONLY_EMPLOYEES_MARK_READY);

        OrderModel order = fetchOrderOrThrow(orderId, OrderMessages.ORDER_NOT_FOUND);

        if (order.getStatus() != OrderStatus.EN_PREPARACION) {
            throw new DomainException(OrderMessages.ORDER_NOT_IN_PREPARATION);
        }

        Long employeeId = authContextPort.getAuthenticatedUserId();
        if (!employeeId.equals(order.getChefId())) {
            throw new DomainException(OrderMessages.ONLY_CHEF_CAN_MARK);
        }

        String oldStatus = order.getStatus().name();

        String pin = generateSecurityPin();
        order.setSecurityPin(pin);
        order.setStatus(OrderStatus.LISTO);

        UserModel client = userExternalPort.getUserById(order.getClientId());
        String message = OrderMessages.READY_MESSAGE_PREFIX + pin;
        messagingExternalPort.sendMessage(client.getPhone(), message);

        orderPersistencePort.saveOrder(order);

        sendTrace(order, oldStatus, OrderStatus.LISTO.name());
    }

    @Override
    public void deliverOrder(Long orderId, String pin) {

        requireRole(OrderConstants.ROLE_EMPLEADO, OrderMessages.ONLY_EMPLOYEES_DELIVER);

        OrderModel order = fetchOrderOrThrow(orderId, OrderMessages.ORDER_NOT_FOUND);

        if (order.getStatus() != OrderStatus.LISTO) {
            throw new DomainException(OrderMessages.ORDER_NOT_READY_FOR_DELIVERY);
        }

        if (!order.getSecurityPin().equals(pin)) {
            throw new DomainException(OrderMessages.INVALID_SECURITY_PIN);
        }

        String oldStatus = order.getStatus().name();
        order.setStatus(OrderStatus.ENTREGADO);
        order.setSecurityPin(null);

        orderPersistencePort.saveOrder(order);
        sendTrace(order, oldStatus, OrderStatus.ENTREGADO.name());
    }

    @Override
    public void cancelOrder(Long orderId) {

        requireRole(OrderConstants.ROLE_CLIENTE, OrderMessages.ONLY_CLIENTS_CANCEL);

        OrderModel order = fetchOrderOrThrow(orderId, OrderMessages.ORDER_NOT_FOUND);

        Long clientId = authContextPort.getAuthenticatedUserId();
        if (!order.getClientId().equals(clientId)) {
            throw new DomainException(OrderMessages.ONLY_OWNERS_CANCEL);
        }

        if (order.getStatus() != OrderStatus.PENDIENTE) {
            throw new DomainException(OrderMessages.CANNOT_CANCEL_IN_PREPARATION);
        }

        String oldStatus = order.getStatus().name();

        order.setStatus(OrderStatus.CANCELADO);
        orderPersistencePort.saveOrder(order);
        sendTrace(order, oldStatus, OrderStatus.CANCELADO.name());
    }

    private void requireRole(String requiredRole, String errorMessage) {
        String callerRole = authContextPort.getAuthenticatedUserRole();
        if (!requiredRole.equals(callerRole)) {
            throw new DomainException(errorMessage);
        }
    }

    private OrderModel fetchOrderOrThrow(Long orderId, String message) {
        OrderModel order = orderPersistencePort.findById(orderId);
        if (order == null) {
            throw new DomainException(message);
        }
        return order;
    }

    private String generateSecurityPin() {
        int max = (int) Math.pow(10, OrderConstants.PIN_LENGTH);
        int value = ThreadLocalRandom.current().nextInt(0, max);
        return String.format("%0" + OrderConstants.PIN_LENGTH + "d", value);
    }

    private void sendTrace(OrderModel order, String oldStatus, String newStatus) {
        TraceLogModel trace = new TraceLogModel(
                order.getId(),
                order.getClientId(),
                oldStatus,
                newStatus,
                authContextPort.getAuthenticatedUserId()
        );
        traceExternalPort.saveOrderTrace(trace);
    }
}
