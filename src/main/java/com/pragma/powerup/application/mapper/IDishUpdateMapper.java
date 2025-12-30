package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.DishUpdateDto;
import com.pragma.powerup.domain.model.DishModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IDishUpdateMapper {

    // Convierte el DTO de actualización (precio, descripción) al modelo
    DishModel toModel(DishUpdateDto dishUpdateDto);
}