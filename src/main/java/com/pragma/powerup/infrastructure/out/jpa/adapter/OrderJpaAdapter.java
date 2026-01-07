package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.GenericPage;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {
    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    @Override
    public void saveOrder(OrderModel orderModel) {
        OrderEntity orderEntity = orderEntityMapper.toEntity(orderModel);

        // En relaciones bidireccionales de JPA, cada detalle debe conocer a su padre
        orderEntity.getOrderDishes().forEach(dish -> dish.setOrder(orderEntity));

        orderRepository.save(orderEntity);
    }

    @Override
    public boolean existsByClientIdAndStatusIn(Long clientId, List<OrderStatus> statuses) {
        // Convertimos la lista de Enums a Strings para el query de BD
        List<String> statusStrings = statuses.stream()
                .map(Enum::name)
                .collect(Collectors.toList());
        return orderRepository.existsByClientIdAndStatusIn(clientId, statusStrings);
    }

    @Override
    public GenericPage<OrderModel> getOrdersByStatusAndRestaurant(OrderStatus status, Long restaurantId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderEntity> pageResult = orderRepository.findAllByStatusAndRestaurantId(status.name(), restaurantId, pageable);

        List<OrderModel> content = pageResult.map(orderEntityMapper::toModel).getContent();

        GenericPage<OrderModel> result = new GenericPage<>();
        result.setContent(content);
        result.setPageNumber(pageResult.getNumber());
        result.setPageSize(pageResult.getSize());
        result.setTotalElements(pageResult.getTotalElements());
        result.setTotalPages(pageResult.getTotalPages());
        result.setFirst(pageResult.isFirst());
        result.setLast(pageResult.isLast());
        result.setHasNext(!pageResult.isLast());
        result.setHasPrevious(!pageResult.isFirst());
        return result;
    }
}