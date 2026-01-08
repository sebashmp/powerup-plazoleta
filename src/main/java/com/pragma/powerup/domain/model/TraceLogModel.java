package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TraceLogModel {
    private Long orderId;
    private Long clientId;
    private String previousStatus;
    private String newStatus;
    private Long employeeId;
}