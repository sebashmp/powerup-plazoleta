package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.DishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDishRepository extends JpaRepository<DishEntity, Long> {
    // Filtra por restaurante, por categoría (si viene) y que el plato esté activo
    Page<DishEntity> findAllByRestaurantIdAndActiveTrue(Long restaurantId, Pageable pageable);

    Page<DishEntity> findAllByRestaurantIdAndCategoryIdAndActiveTrue(Long restaurantId, Long categoryId, Pageable pageable);
}