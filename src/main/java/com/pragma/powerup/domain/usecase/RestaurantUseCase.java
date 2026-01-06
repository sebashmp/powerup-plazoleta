package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.GenericPage;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IAuthenticationContextPort;
import com.pragma.powerup.domain.spi.IEmployeeRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserExternalPort;

public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserExternalPort userExternalPort;
    private final IAuthenticationContextPort authContextPort;
    private final IEmployeeRestaurantPersistencePort employeeRestaurantPersistencePort;
    private final String ADMIN_ROLE = "ROLE_ADMIN";

    public RestaurantUseCase(IRestaurantPersistencePort restaurantPersistencePort,
                             IUserExternalPort userExternalPort,
                             IAuthenticationContextPort authContextPort,
                             IEmployeeRestaurantPersistencePort employeeRestaurantPersistencePort) {
        this.employeeRestaurantPersistencePort = employeeRestaurantPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.userExternalPort = userExternalPort;
        this.authContextPort = authContextPort;
    }

    @Override
    public void saveRestaurant(RestaurantModel restaurantModel) {
        // 1. REGLA DE NEGOCIO: Validar que el que llama sea Administrador
        String callerRole = authContextPort.getAuthenticatedUserRole();
        if (!ADMIN_ROLE.equals(callerRole)) {
            throw new DomainException("Only an administrator can create a restaurant.");
        }

        validateRestaurantRules(restaurantModel);
        restaurantPersistencePort.saveRestaurant(restaurantModel);
    }

    @Override
    public GenericPage<RestaurantModel> getRestaurants(Integer page, Integer size) {
        return restaurantPersistencePort.getAllRestaurants(page, size);
    }

    @Override
    public void linkEmployeeToRestaurant(Long restaurantId, Long employeeId) {
        // 1. Obtener el restaurante
        RestaurantModel restaurant = restaurantPersistencePort.getRestaurantById(restaurantId);
        if (restaurant == null) {
            throw new DomainException("The restaurant does not exist.");
        }

        // 2. REGLA DE NEGOCIO: Solo el dueño puede vincular empleados
        Long authenticatedOwnerId = authContextPort.getAuthenticatedUserId();
        if (!restaurant.getOwnerId().equals(authenticatedOwnerId)) {
            throw new DomainException("Only the owner of the restaurant can link employees to it.");
        }

        // 3. Persistir el vínculo
        employeeRestaurantPersistencePort.saveEmployeeRestaurant(employeeId, restaurantId);
    }

    private void validateRestaurantRules(RestaurantModel restaurant) {
        // 2. REGLA DE NEGOCIO: Validar que el ID asignado sea un Propietario (Llamada Feign)
        if (!userExternalPort.isOwnerUser(restaurant.getOwnerId())) {
            throw new DomainException("The provided owner ID does not belong to a user with the 'Owner' role.");
        }

        // 3. NIT y Teléfono únicamente numéricos
        if (!restaurant.getNit().matches("\\d+")) {
            throw new DomainException("NIT must be numeric.");
        }

        // Validación del nombre (no solo números)
        if (restaurant.getName().matches("\\d+")) {
            throw new DomainException("Restaurant name cannot consist only of numbers.");
        }

        // Validación del teléfono (max 13 y numérico)
        if (!restaurant.getPhone().matches("\\+?\\d+") || restaurant.getPhone().length() > 13) {
            throw new DomainException("Invalid phone format or length.");
        }
    }
}