package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.GenericPage;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.UserModel;
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
    private final IUserExternalPort userExternalPort;
    private final IMessagingExternalPort messagingExternalPort;

    public OrderUseCase(IOrderPersistencePort orderPersistencePort,
                        IRestaurantPersistencePort restaurantPersistencePort,
                        IDishPersistencePort dishPersistencePort,
                        IAuthenticationContextPort authContextPort,
                        IEmployeeRestaurantPersistencePort employeeRestaurantPersistencePort,
                        IUserExternalPort userExternalPort,
                        IMessagingExternalPort messagingExternalPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.authContextPort = authContextPort;
        this.employeeRestaurantPersistencePort = employeeRestaurantPersistencePort;
        this.userExternalPort = userExternalPort;
        this.messagingExternalPort = messagingExternalPort;
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
    public GenericPage<OrderModel> getOrdersByStatus(OrderStatus status, Integer page, Integer size) {
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

    @Override
    public void assignOrder(Long orderId) {
        // 1. Validar que el que llama sea un Empleado
        String role = authContextPort.getAuthenticatedUserRole();
        if (!"ROLE_EMPLEADO".equals(role)) {
            throw new DomainException("Only employees can assign themselves to orders.");
        }

        // 2. Obtener el ID del empleado y su restaurante
        Long employeeId = authContextPort.getAuthenticatedUserId();
        Long restaurantId = employeeRestaurantPersistencePort.getRestaurantIdByEmployeeId(employeeId);

        // 3. Buscar el pedido
        OrderModel order = orderPersistencePort.findById(orderId);
        if (order == null) {
            throw new DomainException("The order does not exist.");
        }

        // 4. REGLA: El pedido debe estar en estado PENDIENTE para ser tomado
        if (order.getStatus() != OrderStatus.PENDIENTE) {
            throw new DomainException("Only pending orders can be assigned.");
        }

        // 5. REGLA: El pedido debe ser del mismo restaurante que el empleado
        if (!order.getRestaurant().getId().equals(restaurantId)) {
            throw new DomainException("You can only assign yourself to orders from your own restaurant.");
        }

        // 6. Asignar empleado y cambiar estado
        order.setChefId(employeeId);
        order.setStatus(OrderStatus.EN_PREPARACION);

        // 7. Guardar cambios
        orderPersistencePort.saveOrder(order);
    }

    @Override
    public void markOrderAsReady(Long orderId) {
        // 1. Validar que el que llama sea EMPLEADO
        String role = authContextPort.getAuthenticatedUserRole();
        if (!"ROLE_EMPLEADO".equals(role)) {
            throw new DomainException("Only employees can mark orders as ready.");
        }

        // 2. Buscar el pedido
        OrderModel order = orderPersistencePort.findById(orderId);
        if (order == null) throw new DomainException("Order not found.");

        // 3. REGLA: Solo pedidos EN_PREPARACION pueden pasar a LISTO
        if (order.getStatus() != OrderStatus.EN_PREPARACION) {
            throw new DomainException("The order is not in preparation.");
        }

        // 4. REGLA: Solo el CHEF asignado puede marcarlo como listo
        Long employeeId = authContextPort.getAuthenticatedUserId();
        if (!order.getChefId().equals(employeeId)) {
            throw new DomainException("You can only mark as ready orders assigned to you.");
        }

        // 5. Generar PIN de seguridad (6 dígitos aleatorios)
        String pin = String.format("%06d", (int)(Math.random() * 1000000));
        order.setSecurityPin(pin);
        order.setStatus(OrderStatus.LISTO);

        // 6. Obtener datos del cliente (Necesitamos su teléfono desde el microservicio Usuarios)
        UserModel client = userExternalPort.getUserById(order.getClientId());

        // 7. Enviar notificación a través del puerto de mensajería
        String message = "Tu pedido está listo! Reclámalo con el PIN: " + pin;
        messagingExternalPort.sendMessage(client.getPhone(), message);

        // 8. Persistir cambios
        orderPersistencePort.saveOrder(order);
    }

    @Override
    public void deliverOrder(Long orderId, String pin) {
        // 1. Validar Rol
        if (!"ROLE_EMPLEADO".equals(authContextPort.getAuthenticatedUserRole())) {
            throw new DomainException("Only employees can deliver orders.");
        }

        // 2. Buscar pedido
        OrderModel order = orderPersistencePort.findById(orderId);
        if (order == null) throw new DomainException("Order not found.");

        // 3. REGLA: Solo pedidos en estado LISTO pueden ser entregados
        if (order.getStatus() != OrderStatus.LISTO) {
            throw new DomainException("The order is not ready for delivery.");
        }

        // 4. REGLA: El PIN debe coincidir
        if (!order.getSecurityPin().equals(pin)) {
            throw new DomainException("Invalid security PIN. Delivery rejected.");
        }

        // 5. Cambiar estado y limpiar PIN (por seguridad)
        order.setStatus(OrderStatus.ENTREGADO);
        order.setSecurityPin(null);

        orderPersistencePort.saveOrder(order);
    }
}