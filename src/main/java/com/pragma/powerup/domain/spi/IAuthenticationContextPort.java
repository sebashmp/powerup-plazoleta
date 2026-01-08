package com.pragma.powerup.domain.spi;

public interface IAuthenticationContextPort {
    String getSelectedUserEmail();
    Long getAuthenticatedUserId();
    String getAuthenticatedUserRole();
}