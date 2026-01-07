package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.dto.response.PageResponse;
import com.pragma.powerup.application.handler.IOrderHandler;
import com.pragma.powerup.domain.model.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderRestController {
    private final IOrderHandler orderHandler;

    @PostMapping("/")
    public ResponseEntity<Void> saveOrder(@Valid @RequestBody OrderRequestDto orderRequestDto) {
        orderHandler.saveOrder(orderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Get orders filtered by status (For Employees)")
    @GetMapping("/")
    public ResponseEntity<PageResponse<OrderResponseDto>> getOrdersByStatus(
            @RequestParam OrderStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(orderHandler.getOrdersByStatus(status, page, size));
    }

    @Operation(summary = "Assign an order to the current employee and change status to IN_PREPARATION")
    @PatchMapping("/{id}/assign")
    public ResponseEntity<Void> assignOrder(@PathVariable Long id) {
        orderHandler.assignOrder(id);
        return ResponseEntity.noContent().build();
    }
}