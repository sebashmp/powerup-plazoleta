package com.pragma.powerup.infrastructure.out.feign.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleResponseDto {
    private Long id;
    private String name;
    private String description;
}