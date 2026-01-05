package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IDishServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IAuthenticationContextPort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;

public class DishUseCase implements IDishServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IAuthenticationContextPort authContextPort;

    public DishUseCase(IDishPersistencePort dishPersistencePort, IRestaurantPersistencePort restaurantPersistencePort, IAuthenticationContextPort authContextPort) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.authContextPort = authContextPort;
    }

    @Override
    public void saveDish(DishModel dishModel) {

        Long authenticatedOwnerId = authContextPort.getAuthenticatedUserId();
        // 1. Validar que el restaurante existe
        RestaurantModel restaurant = restaurantPersistencePort.getRestaurantById(dishModel.getRestaurant().getId());
        if (restaurant == null) {
            throw new DomainException("The associated restaurant does not exist.");
        }

        // 2. REGLA DE NEGOCIO: Solo el propietario puede crear platos
        if (!restaurant.getOwnerId().equals(authenticatedOwnerId)) {
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

    @Override
    public void updateDish(Long id, DishModel dishUpdate) {
        // 1. Obtener el plato actual de la BD
        DishModel existingDish = dishPersistencePort.findById(id);
        if (existingDish == null) {
            throw new DomainException("The dish does not exist.");
        }

        // 2. REGLA DE NEGOCIO: El DOMINIO obtiene la identidad del que llama
        Long authenticatedOwnerId = authContextPort.getAuthenticatedUserId();

        // 3. REGLA DE NEGOCIO: Validar propiedad (El dueño del restaurante debe ser quien edita)
        if (!existingDish.getRestaurant().getOwnerId().equals(authenticatedOwnerId)) {
            throw new DomainException("You can only modify dishes from your own restaurant.");
        }

        // 4. REGLA DE NEGOCIO: Solo se puede modificar precio y descripción
        existingDish.setPrice(dishUpdate.getPrice());
        existingDish.setDescription(dishUpdate.getDescription());

        // 5. Validar precio (reutilizamos la regla de la HU3)
        if (existingDish.getPrice() <= 0) {
            throw new DomainException("The price must be a positive integer greater than 0.");
        }

        dishPersistencePort.updateDish(existingDish);
    }

    @Override
    public void changeDishStatus(Long dishId, Boolean active) {
        // 1. Buscar el plato
        DishModel dish = dishPersistencePort.findById(dishId);
        if (dish == null) {
            throw new DomainException("The dish does not exist.");
        }

        // 2. REGLA DE NEGOCIO: El DOMINIO obtiene la identidad del que llama
        Long authenticatedOwnerId = authContextPort.getAuthenticatedUserId();

        // 3. REGLA DE NEGOCIO: Validar que el dueño del restaurante sea quien modifica
        if (!dish.getRestaurant().getOwnerId().equals(authenticatedOwnerId)) {
            throw new DomainException("You can only change status for dishes from your own restaurant.");
        }

        // 4. Aplicar cambio y guardar
        dish.setActive(active);
        dishPersistencePort.updateDish(dish);
    }
}