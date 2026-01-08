package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.util.RestaurantConstants;
import com.pragma.powerup.domain.util.RestaurantMessages;
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
        requireRole(RestaurantConstants.ROLE_ADMIN, RestaurantMessages.ONLY_ADMIN_CREATE);

        validateRestaurantRules(restaurantModel);

        restaurantPersistencePort.saveRestaurant(restaurantModel);
    }

    @Override
    public GenericPage<RestaurantModel> getRestaurants(Integer page, Integer size) {
        return restaurantPersistencePort.getAllRestaurants(page, size);
    }

    @Override
    public void linkEmployeeToRestaurant(Long restaurantId, Long employeeId) {
        RestaurantModel restaurant = fetchRestaurantOrThrow(restaurantId, RestaurantMessages.RESTAURANT_NOT_FOUND);

        Long authenticatedOwnerId = authContextPort.getAuthenticatedUserId();
        if (!restaurant.getOwnerId().equals(authenticatedOwnerId)) {
            throw new DomainException(RestaurantMessages.ONLY_OWNER_LINK_EMPLOYEE);
        }

        employeeRestaurantPersistencePort.saveEmployeeRestaurant(employeeId, restaurantId);
    }

    private void validateRestaurantRules(RestaurantModel restaurant) {
        // 1) Owner must be a real owner in Users service
        if (!userExternalPort.isOwnerUser(restaurant.getOwnerId())) {
            throw new DomainException(RestaurantMessages.OWNER_ID_NOT_OWNER);
        }

        // 2) NIT numeric
        if (!restaurant.getNit().matches(RestaurantConstants.NIT_REGEX)) {
            throw new DomainException(RestaurantMessages.NIT_MUST_BE_NUMERIC);
        }

        // 3) Name must not be only numeric
        if (restaurant.getName() == null || restaurant.getName().matches(RestaurantConstants.NUMERIC_ONLY_REGEX)) {
            throw new DomainException(RestaurantMessages.NAME_CANNOT_BE_NUMERIC);
        }

        // 4) Phone format and length
        if (restaurant.getPhone() == null
                || !restaurant.getPhone().matches(RestaurantConstants.PHONE_REGEX)
                || restaurant.getPhone().length() > RestaurantConstants.PHONE_MAX_LENGTH) {
            throw new DomainException(RestaurantMessages.INVALID_PHONE_FORMAT);
        }
    }

    private void requireRole(String requiredRole, String errorMessage) {
        String callerRole = authContextPort.getAuthenticatedUserRole();
        if (!requiredRole.equals(callerRole)) {
            throw new DomainException(errorMessage);
        }
    }

    private RestaurantModel fetchRestaurantOrThrow(Long restaurantId, String message) {
        RestaurantModel restaurant = restaurantPersistencePort.getRestaurantById(restaurantId);
        if (restaurant == null) {
            throw new DomainException(message);
        }
        return restaurant;
    }
}
