package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.DishStatusRequestDto;
import com.pragma.powerup.application.dto.request.DishUpdateDto;
import com.pragma.powerup.application.handler.IDishHandler;
import com.pragma.powerup.application.mapper.IDishRequestMapper;
import com.pragma.powerup.application.mapper.IDishUpdateMapper;
import com.pragma.powerup.domain.api.IDishServicePort;
import com.pragma.powerup.domain.model.DishModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DishHandler implements IDishHandler {

    private final IDishServicePort dishServicePort;
    private final IDishRequestMapper dishRequestMapper;
    private final IDishUpdateMapper dishUpdateMapper;

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
}
