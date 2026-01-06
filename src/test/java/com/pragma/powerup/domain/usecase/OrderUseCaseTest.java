package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.IAuthenticationContextPort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
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

    @InjectMocks
    private OrderUseCase orderUseCase;

    private OrderModel orderModel;
    private RestaurantModel restaurant;
    private DishModel dish;
    private Long clientId = 10L;

    @BeforeEach
    void setUp() {
        restaurant = new RestaurantModel();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");

        dish = new DishModel();
        dish.setId(100L);
        dish.setName("Test Dish");
        dish.setRestaurant(restaurant); // El plato pertenece al restaurante 1

        OrderDishModel orderDish = new OrderDishModel();
        orderDish.setDish(dish);
        orderDish.setQuantity(2);

        orderModel = new OrderModel();
        orderModel.setRestaurant(restaurant);
        orderModel.setOrderDishes(Collections.singletonList(orderDish));
    }

    @Test
    @DisplayName("Should save order successfully when all validations pass")
    void saveOrder_Success() {
        // Arrange
        when(authContextPort.getAuthenticatedUserId()).thenReturn(clientId);
        when(orderPersistencePort.existsByClientIdAndStatusIn(eq(clientId), anyList())).thenReturn(false);
        when(restaurantPersistencePort.getRestaurantById(1L)).thenReturn(restaurant);
        when(dishPersistencePort.findById(100L)).thenReturn(dish);

        // Act
        orderUseCase.saveOrder(orderModel);

        // Assert
        assertEquals(OrderStatus.PENDIENTE, orderModel.getStatus());
        assertNotNull(orderModel.getDate());
        assertEquals(clientId, orderModel.getClientId());
        verify(orderPersistencePort).saveOrder(orderModel);
    }

    @Test
    @DisplayName("Should throw exception when client already has an active order")
    void saveOrder_ActiveOrderExists_ThrowsException() {
        // Arrange
        when(authContextPort.getAuthenticatedUserId()).thenReturn(clientId);
        when(orderPersistencePort.existsByClientIdAndStatusIn(eq(clientId), anyList())).thenReturn(true);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> orderUseCase.saveOrder(orderModel));
        assertEquals("You already have an active order in progress.", exception.getMessage());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Should throw exception when restaurant does not exist")
    void saveOrder_RestaurantNotFound_ThrowsException() {
        // Arrange
        when(authContextPort.getAuthenticatedUserId()).thenReturn(clientId);
        when(orderPersistencePort.existsByClientIdAndStatusIn(anyLong(), anyList())).thenReturn(false);
        when(restaurantPersistencePort.getRestaurantById(anyLong())).thenReturn(null);

        // Act & Assert
        assertThrows(DomainException.class, () -> orderUseCase.saveOrder(orderModel));
    }

    @Test
    @DisplayName("Should throw exception when a dish belongs to another restaurant")
    void saveOrder_DishFromDifferentRestaurant_ThrowsException() {
        // Arrange
        RestaurantModel otherRestaurant = new RestaurantModel();
        otherRestaurant.setId(2L); // ID Diferente

        DishModel intruderDish = new DishModel();
        intruderDish.setId(100L);
        intruderDish.setRestaurant(otherRestaurant); // Este plato es del restaurante 2

        when(authContextPort.getAuthenticatedUserId()).thenReturn(clientId);
        when(orderPersistencePort.existsByClientIdAndStatusIn(anyLong(), anyList())).thenReturn(false);
        when(restaurantPersistencePort.getRestaurantById(1L)).thenReturn(restaurant);
        when(dishPersistencePort.findById(100L)).thenReturn(intruderDish);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> orderUseCase.saveOrder(orderModel));
        assertEquals("One or more dishes do not belong to the selected restaurant.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when dish ID does not exist")
    void saveOrder_DishNotFound_ThrowsException() {
        // Arrange
        when(authContextPort.getAuthenticatedUserId()).thenReturn(clientId);
        when(orderPersistencePort.existsByClientIdAndStatusIn(anyLong(), anyList())).thenReturn(false);
        when(restaurantPersistencePort.getRestaurantById(1L)).thenReturn(restaurant);
        when(dishPersistencePort.findById(anyLong())).thenReturn(null);

        // Act & Assert
        assertThrows(DomainException.class, () -> orderUseCase.saveOrder(orderModel));
    }
}