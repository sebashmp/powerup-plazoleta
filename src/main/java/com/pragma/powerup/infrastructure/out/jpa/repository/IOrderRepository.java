package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {
    // Para la regla: Un solo pedido activo por cliente
    boolean existsByClientIdAndStatusIn(Long clientId, List<String> statuses);
}