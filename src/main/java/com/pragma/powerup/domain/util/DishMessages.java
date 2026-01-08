package com.pragma.powerup.domain.util;

/**
 * Mensajes de error usados en DishUseCase.
 */
public final class DishMessages {

    private DishMessages() {}

    public static final String RESTAURANT_NOT_FOUND =
            "The associated restaurant does not exist.";

    public static final String ONLY_OWNER_CREATE =
            "Only the owner of the restaurant can create dishes.";

    public static final String ONLY_OWNER_UPDATE =
            "You can only modify dishes from your own restaurant.";

    public static final String ONLY_OWNER_CHANGE_STATUS =
            "You can only change status for dishes from your own restaurant.";

    public static final String DISH_NOT_FOUND =
            "The dish does not exist.";

    public static final String PRICE_MUST_BE_POSITIVE =
            "The price must be a positive integer greater than 0.";
}
