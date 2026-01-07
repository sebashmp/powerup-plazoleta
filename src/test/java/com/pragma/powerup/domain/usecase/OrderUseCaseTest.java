package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.*;
import com.pragma.powerup.domain.usecase.factory.OrderTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pragma.powerup.domain.usecase.factory.OrderTestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    @Mock
    private IOrderPersistencePort orderPersistencePort;
    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;
    @Mock
    private IDishPersistencePort dishPersistencePort;
    @Mock
    private IAuthenticationContextPort authContextPort;
    @Mock
    private IEmployeeRestaurantPersistencePort employeeRestaurantPersistencePort;

    @InjectMocks
    private OrderUseCase orderUseCase;

    private OrderModel order;

    @BeforeEach
    void setUp() {
        order = OrderTestFactory.validOrder();
    }

    @Test
    @DisplayName("Should save order successfully when all validations pass")
    void saveOrder_success() {
        when(authContextPort.getAuthenticatedUserId()).thenReturn(CLIENT_ID);
        when(orderPersistencePort.existsByClientIdAndStatusIn(eq(CLIENT_ID), anyList()))
                .thenReturn(false);
        when(restaurantPersistencePort.getRestaurantById(RESTAURANT_ID))
                .thenReturn(order.getRestaurant());
        when(dishPersistencePort.findById(DISH_ID))
                .thenReturn(order.getOrderDishes().get(0).getDish());

        orderUseCase.saveOrder(order);

        assertEquals(OrderStatus.PENDIENTE, order.getStatus());
        assertNotNull(order.getDate());
        assertEquals(CLIENT_ID, order.getClientId());

        verify(orderPersistencePort).saveOrder(order);
    }

    @Test
    void saveOrder_shouldFailWhenActiveOrderExists() {
        when(authContextPort.getAuthenticatedUserId()).thenReturn(CLIENT_ID);
        when(orderPersistencePort.existsByClientIdAndStatusIn(anyLong(), anyList()))
                .thenReturn(true);

        assertThrows(DomainException.class, () -> orderUseCase.saveOrder(order));

        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void saveOrder_shouldFailWhenDishFromOtherRestaurant() {
        order.getOrderDishes().get(0).setDish(OrderTestFactory.dishFromOtherRestaurant());

        when(authContextPort.getAuthenticatedUserId()).thenReturn(CLIENT_ID);
        when(orderPersistencePort.existsByClientIdAndStatusIn(anyLong(), anyList()))
                .thenReturn(false);
        when(restaurantPersistencePort.getRestaurantById(RESTAURANT_ID))
                .thenReturn(order.getRestaurant());
        when(dishPersistencePort.findById(DISH_ID))
                .thenReturn(order.getOrderDishes().get(0).getDish());

        DomainException ex = assertThrows(
                DomainException.class,
                () -> orderUseCase.saveOrder(order)
        );

        assertEquals(
                "One or more dishes do not belong to the selected restaurant.",
                ex.getMessage()
        );
    }

    @Test
    void getOrders_shouldFailWhenNotEmployee() {
        when(authContextPort.getAuthenticatedUserRole())
                .thenReturn(ROLE_CLIENTE);

        assertThrows(
                DomainException.class,
                () -> orderUseCase.getOrdersByStatus(DEFAULT_STATUS, PAGE, SIZE)
        );
    }

    @Test
    void getOrders_shouldFailWhenEmployeeHasNoRestaurant() {
        when(authContextPort.getAuthenticatedUserRole())
                .thenReturn(ROLE_EMPLEADO);
        when(authContextPort.getAuthenticatedUserId())
                .thenReturn(EMPLOYEE_ID);
        when(employeeRestaurantPersistencePort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                .thenReturn(null);

        assertThrows(
                DomainException.class,
                () -> orderUseCase.getOrdersByStatus(DEFAULT_STATUS, PAGE, SIZE)
        );
    }

    @Test
    void getOrders_shouldReturnPaginatedOrders() {
        when(authContextPort.getAuthenticatedUserRole())
                .thenReturn(ROLE_EMPLEADO);
        when(authContextPort.getAuthenticatedUserId())
                .thenReturn(EMPLOYEE_ID);
        when(employeeRestaurantPersistencePort.getRestaurantIdByEmployeeId(EMPLOYEE_ID))
                .thenReturn(RESTAURANT_ID);
        when(orderPersistencePort.getOrdersByStatusAndRestaurant(
                DEFAULT_STATUS, RESTAURANT_ID, PAGE, SIZE
        )).thenReturn(OrderTestFactory.singleOrderPage());

        GenericPage<OrderModel> result =
                orderUseCase.getOrdersByStatus(DEFAULT_STATUS, PAGE, SIZE);

        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getTotalElements());
    }
}
