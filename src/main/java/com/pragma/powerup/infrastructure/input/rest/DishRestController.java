package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.handler.IDishHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/dish")
@RequiredArgsConstructor
public class DishRestController {

    private final IDishHandler dishHandler;

    @PostMapping("/")
    public ResponseEntity<Void> saveDish(
            @Valid @RequestBody DishRequestDto dishRequestDto,
            @RequestHeader("X-Owner-Id") Long ownerId) { // Simulando el ID del dueño del token
        dishHandler.saveDish(dishRequestDto, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}