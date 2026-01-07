package com.pragma.powerup.infrastructure.out.feign;

import com.pragma.powerup.application.dto.request.SmsRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "messaging-service", url = "localhost:8083/sms")
public interface IMessagingFeignClient {
    @PostMapping("/send")
    void sendSms(@RequestBody SmsRequestDto smsRequestDto);
}