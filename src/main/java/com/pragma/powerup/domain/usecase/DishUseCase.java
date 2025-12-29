package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IDishServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;

public class DishUseCase implements IDishServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;

    public DishUseCase(IDishPersistencePort dishPersistencePort, IRestaurantPersistencePort restaurantPersistencePort) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public void saveDish(DishModel dishModel, Long ownerId) {
        // 1. Validar que el restaurante existe
        RestaurantModel restaurant = restaurantPersistencePort.getRestaurantById(dishModel.getRestaurant().getId());
        if (restaurant == null) {
            throw new DomainException("The associated restaurant does not exist.");
        }

        // 2. REGLA DE NEGOCIO: Solo el propietario puede crear platos
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new DomainException("Only the owner of the restaurant can create dishes.");
        }

        // 3. REGLA DE NEGOCIO: Precio positivo y mayor a 0
        if (dishModel.getPrice() <= 0) {
            throw new DomainException("The price must be a positive integer greater than 0.");
        }

        // 4. REGLA DE NEGOCIO: Por defecto activo en true
        dishModel.setActive(true);
        dishModel.setRestaurant(restaurant); // Aseguramos el objeto completo

        dishPersistencePort.saveDish(dishModel);
    }
}