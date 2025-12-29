package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class RestaurantRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "NIT is required")
    @Pattern(regexp = "\\d+", message = "NIT must be numeric")
    private String nit;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone is required")
    @Size(max = 13, message = "Phone must not exceed 13 characters")
    @Pattern(regexp = "\\+?\\d+", message = "Invalid phone format")
    private String phone;

    @NotBlank(message = "URL Logo is required")
    private String urlLogo;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;
}