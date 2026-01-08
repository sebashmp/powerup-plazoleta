package com.pragma.powerup.domain.util;

public final class RestaurantConstants {

    private RestaurantConstants() {}

    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    // Validaciones
    public static final String NIT_REGEX = "\\d+";
    public static final String PHONE_REGEX = "\\+?\\d+";
    public static final int PHONE_MAX_LENGTH = 13;

    // Mensajes cortos reutilizables
    public static final String NUMERIC_ONLY_REGEX = "\\d+";
}
