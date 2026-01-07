package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderModel {
    private Long id;
    private Long clientId;
    private LocalDate date;
    private OrderStatus status;
    private Long chefId; // Se va a poner después (HU-13)
    private RestaurantModel restaurant;
    private List<OrderDishModel> orderDishes;
    private String securityPin;
}