package com.pragma.powerup.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class OrderResponseDto {
    private Long id;
    private Long clientId;
    private LocalDate date;
    private String status;
    private List<OrderDishResponseDto> orderDishes;
}