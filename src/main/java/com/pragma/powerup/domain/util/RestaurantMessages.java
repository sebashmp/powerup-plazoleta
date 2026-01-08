package com.pragma.powerup.domain.util;

public final class RestaurantMessages {

    private RestaurantMessages() {}

    public static final String ONLY_ADMIN_CREATE = "Only an administrator can create a restaurant.";
    public static final String RESTAURANT_NOT_FOUND = "The restaurant does not exist.";
    public static final String ONLY_OWNER_LINK_EMPLOYEE = "Only the owner of the restaurant can link employees to it.";
    public static final String OWNER_ID_NOT_OWNER = "The provided owner ID does not belong to a user with the 'Owner' role.";
    public static final String NIT_MUST_BE_NUMERIC = "NIT must be numeric.";
    public static final String NAME_CANNOT_BE_NUMERIC = "Restaurant name cannot consist only of numbers.";
    public static final String INVALID_PHONE_FORMAT = "Invalid phone format or length.";
}
