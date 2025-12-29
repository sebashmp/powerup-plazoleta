package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserExternalPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IUserExternalPort userExternalPort;

    @InjectMocks
    private RestaurantUseCase restaurantUseCase;

    private RestaurantModel restaurantModel;

    @BeforeEach
    void setUp() {
        restaurantModel = new RestaurantModel();
        restaurantModel.setName("Restaurante Gourmet 123");
        restaurantModel.setNit("900123456");
        restaurantModel.setAddress("Calle 123 # 45-67");
        restaurantModel.setPhone("+573001234567");
        restaurantModel.setUrlLogo("http://image.com/logo.png");
        restaurantModel.setOwnerId(1L);
    }

    @Test
    @DisplayName("Should save restaurant when all validations pass")
    void saveRestaurant_Success() {
        // Arrange
        when(userExternalPort.isOwnerUser(anyLong())).thenReturn(true);

        // Act
        restaurantUseCase.saveRestaurant(restaurantModel);

        // Assert
        verify(restaurantPersistencePort, times(1)).saveRestaurant(restaurantModel);
    }

    @Test
    @DisplayName("Should throw exception when owner ID does not have the Owner role")
    void saveRestaurant_NotAnOwner_ThrowsException() {
        // Arrange
        when(userExternalPort.isOwnerUser(anyLong())).thenReturn(false);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> restaurantUseCase.saveRestaurant(restaurantModel));

        assertEquals("The provided owner ID does not belong to a user with the 'Owner' role.", exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    @DisplayName("Should throw exception when NIT is not numeric")
    void saveRestaurant_InvalidNit_ThrowsException() {
        // Arrange
        restaurantModel.setNit("900-123-A");
        when(userExternalPort.isOwnerUser(anyLong())).thenReturn(true);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> restaurantUseCase.saveRestaurant(restaurantModel));

        assertTrue(exception.getMessage().contains("NIT must be numeric"));
    }

    @Test
    @DisplayName("Should throw exception when phone is invalid (too long)")
    void saveRestaurant_PhoneTooLong_ThrowsException() {
        // Arrange
        restaurantModel.setPhone("+5730012345678910"); // 16 chars
        when(userExternalPort.isOwnerUser(anyLong())).thenReturn(true);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> restaurantUseCase.saveRestaurant(restaurantModel));

        assertEquals("Phone must not exceed 13 characters.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when restaurant name is only numeric")
    void saveRestaurant_NumericName_ThrowsException() {
        // Arrange
        restaurantModel.setName("12345678");
        when(userExternalPort.isOwnerUser(anyLong())).thenReturn(true);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> restaurantUseCase.saveRestaurant(restaurantModel));

        assertEquals("Restaurant name cannot consist only of numbers.", exception.getMessage());
    }
}