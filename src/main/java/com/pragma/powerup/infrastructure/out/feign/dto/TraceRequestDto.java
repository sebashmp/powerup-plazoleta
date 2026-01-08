package com.pragma.powerup.infrastructure.out.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TraceRequestDto {
    private Long orderId;
    private Long clientId;
    private String previousStatus;
    private String newStatus;
    private Long employeeId;
}