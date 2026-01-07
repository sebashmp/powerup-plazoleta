package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.application.dto.request.SmsRequestDto;
import com.pragma.powerup.domain.spi.IMessagingExternalPort;
import com.pragma.powerup.infrastructure.out.feign.IMessagingFeignClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessagingExternalAdapter implements IMessagingExternalPort {
    private final IMessagingFeignClient messagingFeignClient;

    @Override
    public void sendMessage(String phone, String message) {
        messagingFeignClient.sendSms(new SmsRequestDto(phone, message));
    }
}