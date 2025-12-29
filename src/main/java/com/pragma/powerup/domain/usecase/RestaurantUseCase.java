package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserExternalPort;

public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserExternalPort userExternalPort;

    public RestaurantUseCase(IRestaurantPersistencePort restaurantPersistencePort, IUserExternalPort userExternalPort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.userExternalPort = userExternalPort;
    }

    @Override
    public void saveRestaurant(RestaurantModel restaurantModel) {
        validateRestaurantRules(restaurantModel);
        restaurantPersistencePort.saveRestaurant(restaurantModel);
    }

    private void validateRestaurantRules(RestaurantModel restaurant) {
        // 1. Validar que el dueño sea realmente un Propietario (Uso del puerto externo)
        if (!userExternalPort.isOwnerUser(restaurant.getOwnerId())) {
            throw new DomainException("The provided owner ID does not belong to a user with the 'Owner' role.");
        }

        // 2. NIT y Teléfono únicamente numéricos
        if (!restaurant.getNit().matches("\\d+")) {
            throw new DomainException("NIT must be numeric.");
        }
        if (!restaurant.getPhone().matches("\\+?\\d+")) {
            throw new DomainException("Phone must be numeric and can start with +.");
        }

        // 3. Teléfono máximo 13 caracteres
        if (restaurant.getPhone().length() > 13) {
            throw new DomainException("Phone must not exceed 13 characters.");
        }

        // 4. Nombre no puede ser sólo números
        if (restaurant.getName().matches("\\d+")) {
            throw new DomainException("Restaurant name cannot consist only of numbers.");
        }
    }
}