package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DishRequestDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private Integer price;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotBlank(message = "URL Image is required")
    private String urlImage;
}