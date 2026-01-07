package com.pragma.powerup.domain.spi;

public interface IMessagingExternalPort {
    void sendMessage(String phone, String message);
}