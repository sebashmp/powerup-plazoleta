package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.GenericPage;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.DishEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class DishJpaAdapter implements IDishPersistencePort {

    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;

    @Override
    public void saveDish(DishModel dishModel) {
        dishRepository.save(dishEntityMapper.toEntity(dishModel));
    }

    @Override
    public void updateDish(DishModel dishModel) {
        dishRepository.save(dishEntityMapper.toEntity(dishModel));
    }

    @Override
    public DishModel findById(Long id) {
        // Buscamos en el repo, si existe mapeamos a modelo, si no retornamos null
        return dishRepository.findById(id)
                .map(dishEntityMapper::toModel)
                .orElse(null);
    }

    @Override
    public GenericPage<DishModel> getDishesByRestaurant(Long restaurantId, Long categoryId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DishEntity> dishPage;

        if (categoryId != null) {
            dishPage = dishRepository.findAllByRestaurantIdAndCategoryIdAndActiveTrue(restaurantId, categoryId, pageable);
        } else {
            dishPage = dishRepository.findAllByRestaurantIdAndActiveTrue(restaurantId, pageable);
        }

        List<DishModel> content = dishPage.map(dishEntityMapper::toModel).getContent();

        GenericPage<DishModel> result = new GenericPage<>();
        result.setContent(content);
        result.setPageNumber(dishPage.getNumber());
        result.setPageSize(dishPage.getSize());
        result.setTotalElements(dishPage.getTotalElements());
        result.setTotalPages(dishPage.getTotalPages());
        result.setFirst(dishPage.isFirst());
        result.setLast(dishPage.isLast());
        result.setHasNext(!dishPage.isLast());
        result.setHasPrevious(!dishPage.isFirst());

        return result;
    }

}