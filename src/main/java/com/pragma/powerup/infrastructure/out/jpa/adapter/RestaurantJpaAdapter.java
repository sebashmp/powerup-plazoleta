package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@RequiredArgsConstructor
public class RestaurantJpaAdapter implements IRestaurantPersistencePort {
    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    @Override
    public void saveRestaurant(RestaurantModel restaurantModel) {
        restaurantRepository.save(restaurantEntityMapper.toEntity(restaurantModel));
    }
    @Override
    public RestaurantModel getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .map(restaurantEntityMapper::toModel)
                .orElse(null); // Handle null or throw an exception as needed
    }

    @Override
    public List<RestaurantModel> getAllRestaurants(Integer page, Integer size) {
        // Creamos el objeto de paginación: página, tamaño y ordenado por nombre ASC
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        return restaurantRepository.findAll(pageable)
                .map(restaurantEntityMapper::toModel)
                .getContent(); // Extraemos la lista del objeto Page
    }
}
