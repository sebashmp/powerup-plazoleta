package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.PageResponse;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.handler.IRestaurantHandler;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
public class RestaurantRestController {

    private final IRestaurantHandler restaurantHandler;

    @PostMapping("/")
    public ResponseEntity<Void> saveRestaurant(@Valid @RequestBody RestaurantRequestDto restaurantRequestDto) {
        restaurantHandler.saveRestaurant(restaurantRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "List all restaurants alphabetically and paginated")
    @GetMapping("/")
    public ResponseEntity<PageResponse<RestaurantResponseDto>> getRestaurants(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(restaurantHandler.getRestaurants(page, size));
    }
}
