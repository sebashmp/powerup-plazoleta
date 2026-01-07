package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class OrderDeliveryRequestDto {
    @NotBlank(message = "The security PIN is required")
    private String pin;
}