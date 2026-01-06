package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IAuthenticationContextPort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final IAuthenticationContextPort authContextPort;

    public OrderUseCase(IOrderPersistencePort orderPersistencePort,
                        IRestaurantPersistencePort restaurantPersistencePort,
                        IDishPersistencePort dishPersistencePort,
                        IAuthenticationContextPort authContextPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.authContextPort = authContextPort;
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
}