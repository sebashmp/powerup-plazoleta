package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.application.dto.response.PageResponse;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.handler.impl.RestaurantHandler;
import com.pragma.powerup.application.mapper.IRestaurantResponseMapper;
import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.GenericPage;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IAuthenticationContextPort;
import com.pragma.powerup.domain.spi.IUserExternalPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IUserExternalPort userExternalPort;
    @Mock
    private IAuthenticationContextPort authContextPort;

    @InjectMocks
    private RestaurantUseCase restaurantUseCase;

    private RestaurantModel restaurantModel;

    @Mock
    private IRestaurantServicePort restaurantServicePort;

    @Mock
    private IRestaurantResponseMapper restaurantResponseMapper;

    @InjectMocks
    private RestaurantHandler restaurantHandler;

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
    @DisplayName("Should save restaurant when caller is admin and owner is valid")
    void saveRestaurant_Success() {
        // Arrange
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ROLE_ADMIN"); // Simular Admin
        when(userExternalPort.isOwnerUser(anyLong())).thenReturn(true); // Simular Propietario válido

        // Act
        restaurantUseCase.saveRestaurant(restaurantModel);

        // Assert
        verify(restaurantPersistencePort).saveRestaurant(restaurantModel);
    }

    @Test
    @DisplayName("Should throw exception when caller is NOT admin")
    void saveRestaurant_NotAdmin_ThrowsException() {
        // Arrange
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ROLE_PROPIETARIO");

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> restaurantUseCase.saveRestaurant(restaurantModel));
        assertEquals("Only an administrator can create a restaurant.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when owner ID does not have the Owner role")
    void saveRestaurant_InvalidOwner_ThrowsException() {
        // Arrange
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ROLE_ADMIN");
        when(userExternalPort.isOwnerUser(anyLong())).thenReturn(false);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> restaurantUseCase.saveRestaurant(restaurantModel));
        assertEquals("The provided owner ID does not belong to a user with the 'Owner' role.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when NIT is not numeric")
    void saveRestaurant_InvalidNit_ThrowsException() {
        // Arrange
        restaurantModel.setNit("900-123-A");
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ROLE_ADMIN");
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
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ROLE_ADMIN");
        when(userExternalPort.isOwnerUser(anyLong())).thenReturn(true);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> restaurantUseCase.saveRestaurant(restaurantModel));

        assertEquals("Invalid phone format or length.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when restaurant name is only numeric")
    void saveRestaurant_NumericName_ThrowsException() {
        // Arrange
        restaurantModel.setName("12345678");
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ROLE_ADMIN");
        when(userExternalPort.isOwnerUser(anyLong())).thenReturn(true);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> restaurantUseCase.saveRestaurant(restaurantModel));

        assertEquals("Restaurant name cannot consist only of numbers.", exception.getMessage());
    }

    @Test
    void getRestaurants_ForwardsParametersAndReturnsGenericPage() {
        // Arrange
        Integer page = 0;
        Integer size = 4;

        RestaurantModel r1 = new RestaurantModel();
        r1.setName("Alba");
        RestaurantModel r2 = new RestaurantModel();
        r2.setName("Beta");

        GenericPage<RestaurantModel> expectedPage = new GenericPage<>(
                List.of(r1, r2),
                page,
                size,
                10L,
                3,
                true,
                false,
                true,
                false
        );

        when(restaurantPersistencePort.getAllRestaurants(page, size)).thenReturn(expectedPage);

        // Act
        GenericPage<RestaurantModel> result = restaurantUseCase.getRestaurants(page, size);

        // Assert
        assertThat(result).isSameAs(expectedPage);
        verify(restaurantPersistencePort, times(1)).getAllRestaurants(page, size);
        verifyNoMoreInteractions(restaurantPersistencePort);
    }

    @Test
    void getRestaurants_ReturnsPageResponseWithProperMetadata() {
        // Arrange
        Integer page = 0;
        Integer size = 3;

        RestaurantModel r1 = new RestaurantModel();
        r1.setName("Alba");
        RestaurantModel r2 = new RestaurantModel();
        r2.setName("Beta");

        GenericPage<RestaurantModel> domainPage = new GenericPage<>(
                List.of(r1, r2),
                page,
                size,
                10L,
                4,
                true,
                false,
                true,
                false
        );

        RestaurantResponseDto dto1 = new RestaurantResponseDto();
        dto1.setName("Alba");
        RestaurantResponseDto dto2 = new RestaurantResponseDto();
        dto2.setName("Beta");

        when(restaurantServicePort.getRestaurants(page, size)).thenReturn(domainPage);
        when(restaurantResponseMapper.toResponseList(domainPage.getContent())).thenReturn(List.of(dto1, dto2));

        // Act
        PageResponse<RestaurantResponseDto> response = restaurantHandler.getRestaurants(page, size);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getPageNumber()).isEqualTo(page);
        assertThat(response.getPageSize()).isEqualTo(size);
        assertThat(response.getTotalElements()).isEqualTo(10L);
        assertThat(response.getTotalPages()).isEqualTo(4);
        assertThat(response.getFirst()).isTrue();
        assertThat(response.getLast()).isFalse();
        assertThat(response.getHasNext()).isTrue();
        assertThat(response.getHasPrevious()).isFalse();

        verify(restaurantServicePort, times(1)).getRestaurants(page, size);
        verify(restaurantResponseMapper, times(1)).toResponseList(domainPage.getContent());
        verifyNoMoreInteractions(restaurantServicePort, restaurantResponseMapper);
    }
}