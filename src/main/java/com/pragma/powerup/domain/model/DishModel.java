package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DishModel {
    private Long id;
    private String name;
    private CategoryModel category;
    private String description;
    private Integer price;
    private RestaurantModel restaurant;
    private String urlImage;
    private Boolean active;
}