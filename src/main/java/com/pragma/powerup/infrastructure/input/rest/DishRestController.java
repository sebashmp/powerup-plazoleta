package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.DishStatusRequestDto;
import com.pragma.powerup.application.dto.request.DishUpdateDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.handler.IDishHandler;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/dish")
@RequiredArgsConstructor
public class DishRestController {

    private final IDishHandler dishHandler;

    @PostMapping("/")
    public ResponseEntity<Void> saveDish(
            @Valid @RequestBody DishRequestDto dishRequestDto) {
        dishHandler.saveDish(dishRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Update an existing dish")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDish(
            @PathVariable Long id,
            @Valid @RequestBody DishUpdateDto dishUpdateDto) {
        dishHandler.updateDish(id, dishUpdateDto);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @Operation(summary = "Enable or disable a dish")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeDishStatus(
            @PathVariable Long id,
            @Valid @RequestBody DishStatusRequestDto dishStatusRequestDto) {
        dishHandler.changeDishStatus(id, dishStatusRequestDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get restaurant menu paginated and filtered by category")
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<DishResponseDto>> getMenu(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(dishHandler.getDishesByRestaurant(restaurantId, categoryId, page, size));
    }
}