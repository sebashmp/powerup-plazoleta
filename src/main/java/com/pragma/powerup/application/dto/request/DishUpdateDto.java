package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DishUpdateDto {
    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private Integer price;

    @NotBlank(message = "Description is required")
    private String description;
}