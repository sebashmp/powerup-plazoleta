package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final IAuthenticationContextPort authContextPort;
    private final IEmployeeRestaurantPersistencePort employeeRestaurantPersistencePort;

    public OrderUseCase(IOrderPersistencePort orderPersistencePort,
                        IRestaurantPersistencePort restaurantPersistencePort,
                        IDishPersistencePort dishPersistencePort,
                        IAuthenticationContextPort authContextPort,
                        IEmployeeRestaurantPersistencePort employeeRestaurantPersistencePort) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.authContextPort = authContextPort;
        this.employeeRestaurantPersistencePort = employeeRestaurantPersistencePort;
    }

    @Override
    public void saveOrder(OrderModel orderModel) {
        // 1. Obtener el ID del cliente autenticado
        Long clientId = authContextPort.getAuthenticatedUserId();
        orderModel.setClientId(clientId);

        // 2. REGLA: El cliente no debe tener pedidos en proceso
        List<OrderStatus> activeStatuses = Arrays.asList(
                OrderStatus.PENDIENTE,
                OrderStatus.EN_PREPARACION,
                OrderStatus.LISTO
        );
        if (orderPersistencePort.existsByClientIdAndStatusIn(clientId, activeStatuses)) {
            throw new DomainException("You already have an active order in progress.");
        }

        // 3. REGLA: Validar que el restaurante existe
        if (restaurantPersistencePort.getRestaurantById(orderModel.getRestaurant().getId()) == null) {
            throw new DomainException("The specified restaurant does not exist.");
        }

        // 4. REGLA: Validar que todos los platos pertenecen al restaurante
        orderModel.getOrderDishes().forEach(orderDish -> {
            var dish = dishPersistencePort.findById(orderDish.getDish().getId());
            if (dish == null || !dish.getRestaurant().getId().equals(orderModel.getRestaurant().getId())) {
                throw new DomainException("One or more dishes do not belong to the selected restaurant.");
            }
            // Inyectamos el plato completo al detalle para persistencia posterior
            orderDish.setDish(dish);
        });

        // 5. Configuración inicial del pedido
        orderModel.setDate(LocalDate.now());
        orderModel.setStatus(OrderStatus.PENDIENTE);

        orderPersistencePort.saveOrder(orderModel);
    }

    @Override
    public List<OrderModel> getOrdersByStatus(OrderStatus status, Integer page, Integer size) {
        // 1. Validar que el que llama es un Empleado
        String callerRole = authContextPort.getAuthenticatedUserRole();
        if (!"ROLE_EMPLEADO".equals(callerRole)) {
            throw new DomainException("Only employees can access this service.");
        }

        // 2. Obtener el ID del empleado desde el Token
        Long employeeId = authContextPort.getAuthenticatedUserId();

        // 3. REGLA DE NEGOCIO: Buscar a qué restaurante pertenece este empleado
        Long restaurantId = employeeRestaurantPersistencePort.getRestaurantIdByEmployeeId(employeeId);
        if (restaurantId == null) {
            throw new DomainException("The employee is not assigned to any restaurant.");
        }

        // 4. Retornar los pedidos filtrados por ese restaurante y el estado solicitado
        return orderPersistencePort.getOrdersByStatusAndRestaurant(status, restaurantId, page, size);
    }
}