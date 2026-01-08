package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IDishServicePort;
import com.pragma.powerup.domain.util.DishConstants;
import com.pragma.powerup.domain.util.DishMessages;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.GenericPage;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IAuthenticationContextPort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;

public class DishUseCase implements IDishServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IAuthenticationContextPort authContextPort;

    public DishUseCase(IDishPersistencePort dishPersistencePort,
                       IRestaurantPersistencePort restaurantPersistencePort,
                       IAuthenticationContextPort authContextPort) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.authContextPort = authContextPort;
    }

    @Override
    public void saveDish(DishModel dishModel) {
        RestaurantModel restaurant = fetchRestaurantOrThrow(
                dishModel.getRestaurant().getId()
        );

        Long authenticatedOwnerId = authContextPort.getAuthenticatedUserId();
        requireOwner(restaurant.getOwnerId(), authenticatedOwnerId,
                DishMessages.ONLY_OWNER_CREATE);

        validatePrice(dishModel.getPrice());

        dishModel.setActive(DishConstants.DEFAULT_ACTIVE_STATUS);
        dishModel.setRestaurant(restaurant);

        dishPersistencePort.upsert(dishModel);
    }

    @Override
    public void updateDish(Long id, DishModel dishUpdate) {
        DishModel existingDish = fetchDishOrThrow(id);

        Long authenticatedOwnerId = authContextPort.getAuthenticatedUserId();
        requireOwner(
                existingDish.getRestaurant().getOwnerId(),
                authenticatedOwnerId,
                DishMessages.ONLY_OWNER_UPDATE
        );

        existingDish.setPrice(dishUpdate.getPrice());
        existingDish.setDescription(dishUpdate.getDescription());

        validatePrice(existingDish.getPrice());

        dishPersistencePort.upsert(existingDish);
    }

    @Override
    public void changeDishStatus(Long dishId, Boolean active) {
        DishModel dish = fetchDishOrThrow(dishId);

        Long authenticatedOwnerId = authContextPort.getAuthenticatedUserId();
        requireOwner(
                dish.getRestaurant().getOwnerId(),
                authenticatedOwnerId,
                DishMessages.ONLY_OWNER_CHANGE_STATUS
        );

        dish.setActive(active);
        dishPersistencePort.upsert(dish);
    }

    @Override
    public GenericPage<DishModel> getDishesByRestaurant(Long restaurantId,
                                                        Long categoryId,
                                                        Integer page,
                                                        Integer size) {
        // Regla de negocio: solo se muestran platos activos (controlado en persistencia)
        return dishPersistencePort.getDishesByRestaurant(
                restaurantId, categoryId, page, size
        );
    }

    /* =========================
       ===== Helper Methods =====
       ========================= */

    private RestaurantModel fetchRestaurantOrThrow(Long restaurantId) {
        RestaurantModel restaurant =
                restaurantPersistencePort.getRestaurantById(restaurantId);

        if (restaurant == null) {
            throw new DomainException(DishMessages.RESTAURANT_NOT_FOUND);
        }
        return restaurant;
    }

    private DishModel fetchDishOrThrow(Long dishId) {
        DishModel dish = dishPersistencePort.findById(dishId);
        if (dish == null) {
            throw new DomainException(DishMessages.DISH_NOT_FOUND);
        }
        return dish;
    }

    private void requireOwner(Long ownerId,
                              Long authenticatedUserId,
                              String errorMessage) {
        if (!ownerId.equals(authenticatedUserId)) {
            throw new DomainException(errorMessage);
        }
    }

    private void validatePrice(Integer price) {
        if (price == null || price <= 0) {
            throw new DomainException(DishMessages.PRICE_MUST_BE_POSITIVE);
        }
    }
}
