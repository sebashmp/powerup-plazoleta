package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.dto.response.PageResponse;
import com.pragma.powerup.application.handler.impl.DishHandler;
import com.pragma.powerup.application.mapper.IDishResponseMapper;
import com.pragma.powerup.domain.api.IDishServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.CategoryModel;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.GenericPage;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IAuthenticationContextPort;
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
class DishUseCaseTest {

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IAuthenticationContextPort authContextPort;

    @InjectMocks
    private DishUseCase dishUseCase;

    @Mock
    private IDishServicePort dishServicePort;

    @Mock
    private IDishResponseMapper dishResponseMapper;

    @InjectMocks
    private DishHandler dishHandler;

    private DishModel dishModel;
    private RestaurantModel restaurantModel;
    private Long ownerId;

    @BeforeEach
    void setUp() {
        ownerId = 1L;

        restaurantModel = new RestaurantModel();
        restaurantModel.setId(10L);
        restaurantModel.setOwnerId(ownerId);
        restaurantModel.setName("Pizzería Test");

        CategoryModel categoryModel = new CategoryModel();
        categoryModel.setId(1L);
        categoryModel.setName("Almuerzos");

        dishModel = new DishModel();
        dishModel.setName("Pizza Margarita");
        dishModel.setPrice(25000);
        dishModel.setDescription("Pizza con tomate y albahaca");
        dishModel.setUrlImage("http://image.com/pizza.png");
        dishModel.setCategory(categoryModel);
        dishModel.setRestaurant(restaurantModel);
    }

    @Test
    @DisplayName("Should save dish successfully when user is the owner")
    void saveDish_Success() {
        // Arrange
        when(authContextPort.getAuthenticatedUserId()).thenReturn(1L);
        when(restaurantPersistencePort.getRestaurantById(10L)).thenReturn(restaurantModel);

        // Act
        dishUseCase.saveDish(dishModel);

        // Assert
        assertTrue(dishModel.getActive(), "Dish should be active by default");
        assertEquals(restaurantModel, dishModel.getRestaurant());
        verify(dishPersistencePort).upsert(dishModel);
    }

    @Test
    @DisplayName("Should throw exception when restaurant does not exist")
    void saveDish_RestaurantNotFound_ThrowsException() {
        // Arrange
        when(restaurantPersistencePort.getRestaurantById(anyLong())).thenReturn(null);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> dishUseCase.saveDish(dishModel));

        assertEquals("The associated restaurant does not exist.", exception.getMessage());
        verify(dishPersistencePort, never()).upsert(any());
    }

    @Test
    @DisplayName("Should throw exception when user is NOT the owner of the restaurant")
    void saveDish_NotTheOwner_ThrowsException() {
        // Arrange
        when(restaurantPersistencePort.getRestaurantById(10L)).thenReturn(restaurantModel);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> dishUseCase.saveDish(dishModel));

        assertEquals("Only the owner of the restaurant can create dishes.", exception.getMessage());
        verify(dishPersistencePort, never()).upsert(any());
    }

    @Test
    @DisplayName("Should throw exception when price is zero or negative")
    void saveDish_InvalidPrice_ThrowsException() {
        // Arrange
        dishModel.setPrice(0);
        when(authContextPort.getAuthenticatedUserId()).thenReturn(ownerId);
        when(restaurantPersistencePort.getRestaurantById(anyLong())).thenReturn(restaurantModel);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> dishUseCase.saveDish(dishModel));

        assertEquals("The price must be a positive integer greater than 0.", exception.getMessage());
    }

    @Test
    @DisplayName("Should update dish successfully when owner is valid")
    void updateDish_Success() {
        // Arrange
        Long dishId = 1L;
        DishModel dishInDb = new DishModel();
        dishInDb.setPrice(100);
        dishInDb.setDescription("Old description");
        RestaurantModel restaurant = new RestaurantModel();
        restaurant.setOwnerId(1L); // El dueño es el ID 1
        dishInDb.setRestaurant(restaurant);

        DishModel dishChanges = new DishModel();
        dishChanges.setPrice(200);
        dishChanges.setDescription("New description");

        when(dishPersistencePort.findById(dishId)).thenReturn(dishInDb);
        when(authContextPort.getAuthenticatedUserId()).thenReturn(1L); // Simula dueño logeado

        // Act
        dishUseCase.updateDish(dishId, dishChanges);

        // Assert
        assertEquals(200, dishInDb.getPrice());
        assertEquals("New description", dishInDb.getDescription());
        verify(dishPersistencePort).upsert(dishInDb);
    }

    @Test
    @DisplayName("Should throw exception when trying to update a non-existent dish")
    void updateDish_DishNotFound_ThrowsException() {
        // Arrange
        Long nonExistentId = 999L;
        DishModel dishChanges = new DishModel();
        dishChanges.setPrice(500);

        when(dishPersistencePort.findById(nonExistentId)).thenReturn(null);

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> dishUseCase.updateDish(nonExistentId, dishChanges));

        assertEquals("The dish does not exist.", exception.getMessage());
        // Verificamos que nunca se llamó al puerto de actualización
        verify(dishPersistencePort, never()).upsert(any());
    }

    @Test
    @DisplayName("Should change status successfully when owner is valid")
    void changeDishStatus_Success() {
        // Arrange
        Long dishId = 1L;
        DishModel dishInDb = new DishModel();
        dishInDb.setActive(true);
        RestaurantModel restaurant = new RestaurantModel();
        restaurant.setOwnerId(1L);
        dishInDb.setRestaurant(restaurant);

        when(dishPersistencePort.findById(dishId)).thenReturn(dishInDb);
        when(authContextPort.getAuthenticatedUserId()).thenReturn(1L);

        // Act
        dishUseCase.changeDishStatus(dishId, false);

        // Assert
        assertFalse(dishInDb.getActive());
        verify(dishPersistencePort).upsert(dishInDb);
    }

    @Test
    @DisplayName("Should throw exception when trying to change status of a dish from another restaurant")
    void changeDishStatus_WrongOwner_ThrowsException() {
        // Arrange
        Long dishId = 1L;
        DishModel dishInDb = new DishModel();
        RestaurantModel restaurant = new RestaurantModel();
        restaurant.setOwnerId(1L); // Dueño real es ID 1
        dishInDb.setRestaurant(restaurant);

        when(dishPersistencePort.findById(dishId)).thenReturn(dishInDb);
        when(authContextPort.getAuthenticatedUserId()).thenReturn(99L); // Intento por ID 99

        // Act & Assert
        assertThrows(DomainException.class, () -> dishUseCase.changeDishStatus(dishId, false));
    }

    @Test
    void getDishesByRestaurant_ForwardsParametersAndReturnsGenericPage() {
        // Arrange
        Long restaurantId = 1L;
        Long categoryId = 2L;
        Integer page = 0;
        Integer size = 5;

        DishModel d1 = new DishModel();
        d1.setName("A");
        DishModel d2 = new DishModel();
        d2.setName("B");

        GenericPage<DishModel> expectedPage = new GenericPage<>(
                List.of(d1, d2),
                page,
                size,
                12L,
                3,
                true,
                false,
                true,
                false
        );

        when(dishPersistencePort.getDishesByRestaurant(restaurantId, categoryId, page, size))
                .thenReturn(expectedPage);

        // Act
        GenericPage<DishModel> result = dishUseCase.getDishesByRestaurant(restaurantId, categoryId, page, size);

        // Assert
        assertThat(result).isSameAs(expectedPage);
        verify(dishPersistencePort, times(1)).getDishesByRestaurant(restaurantId, categoryId, page, size);
        verifyNoMoreInteractions(dishPersistencePort);
    }

    @Test
    void getDishesByRestaurant_MapsGenericPageToPageResponse() {
        // Arrange
        Long restaurantId = 1L;
        Long categoryId = null;
        Integer page = 0;
        Integer size = 2;

        DishModel dm1 = new DishModel();
        dm1.setName("Pizza");
        DishModel dm2 = new DishModel();
        dm2.setName("Ensalada");

        GenericPage<DishModel> domainPage = new GenericPage<>(
                List.of(dm1, dm2),
                page,
                size,
                7L,
                4,
                true,
                false,
                true,
                false
        );

        DishResponseDto dto1 = new DishResponseDto();
        dto1.setName("Pizza");
        DishResponseDto dto2 = new DishResponseDto();
        dto2.setName("Ensalada");

        when(dishServicePort.getDishesByRestaurant(restaurantId, categoryId, page, size)).thenReturn(domainPage);
        when(dishResponseMapper.toResponseList(domainPage.getContent())).thenReturn(List.of(dto1, dto2));

        // Act
        PageResponse<DishResponseDto> response = dishHandler.getDishesByRestaurant(restaurantId, categoryId, page, size);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getPageNumber()).isEqualTo(page);
        assertThat(response.getPageSize()).isEqualTo(size);
        assertThat(response.getTotalElements()).isEqualTo(7L);
        assertThat(response.getTotalPages()).isEqualTo(4);
        assertThat(response.getFirst()).isTrue();
        assertThat(response.getLast()).isFalse();
        assertThat(response.getHasNext()).isTrue();
        assertThat(response.getHasPrevious()).isFalse();

        verify(dishServicePort, times(1)).getDishesByRestaurant(restaurantId, categoryId, page, size);
        verify(dishResponseMapper, times(1)).toResponseList(domainPage.getContent());
        verifyNoMoreInteractions(dishServicePort, dishResponseMapper);
    }


}