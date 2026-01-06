package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.GenericPage;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.RestaurantEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
                .orElse(null); // Handle null
    }

//Select * from restaurants order by name ASC limit 4 offset 0 (en la request de postman)
    @Override
    public GenericPage<RestaurantModel> getAllRestaurants(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<RestaurantEntity> pageResult = restaurantRepository.findAll(pageable);

        List<RestaurantModel> content = pageResult.map(restaurantEntityMapper::toModel).getContent();

        GenericPage<RestaurantModel> result = new GenericPage<>();
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
