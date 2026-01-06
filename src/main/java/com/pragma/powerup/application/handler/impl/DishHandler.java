package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.DishStatusRequestDto;
import com.pragma.powerup.application.dto.request.DishUpdateDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.dto.response.PageResponse;
import com.pragma.powerup.application.handler.IDishHandler;
import com.pragma.powerup.application.mapper.IDishRequestMapper;
import com.pragma.powerup.application.mapper.IDishResponseMapper;
import com.pragma.powerup.application.mapper.IDishUpdateMapper;
import com.pragma.powerup.domain.api.IDishServicePort;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.GenericPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DishHandler implements IDishHandler {

    private final IDishServicePort dishServicePort;
    private final IDishRequestMapper dishRequestMapper;
    private final IDishUpdateMapper dishUpdateMapper;
    private final IDishResponseMapper dishResponseMapper;

    @Override
    public void saveDish(DishRequestDto dishRequestDto) {
        dishServicePort.saveDish(dishRequestMapper.toModel(dishRequestDto));
    }

    @Override
    public void updateDish(Long id, DishUpdateDto dishUpdateDto) {
        DishModel dishModel = dishUpdateMapper.toModel(dishUpdateDto);
        dishServicePort.updateDish(id, dishModel);
    }

    @Override
    public void changeDishStatus(Long dishId, DishStatusRequestDto dishStatusRequestDto) {
        dishServicePort.changeDishStatus(dishId, dishStatusRequestDto.getActive());
    }

    @Override
    public PageResponse<DishResponseDto> getDishesByRestaurant(
            Long restaurantId,
            Long categoryId,
            Integer page,
            Integer size) {

        GenericPage<DishModel> domainPage = dishServicePort.getDishesByRestaurant(restaurantId, categoryId, page, size);

        List<DishResponseDto> dtoContent = dishResponseMapper.toResponseList(domainPage.getContent());

        return new PageResponse<>(
                dtoContent,
                domainPage.getPageNumber(),
                domainPage.getPageSize(),
                domainPage.getTotalElements(),
                domainPage.getTotalPages(),
                domainPage.getFirst(),
                domainPage.getLast(),
                domainPage.getHasNext(),
                domainPage.getHasPrevious()
        );
    }
}
