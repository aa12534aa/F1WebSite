package com.tp.F1WebSite.mappers.impl;

import com.tp.F1WebSite.domain.dto.CircuitDto;
import com.tp.F1WebSite.domain.entities.CircuitEntity;
import com.tp.F1WebSite.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CircuitMapperImpl implements Mapper<CircuitEntity, CircuitDto> {

    private final ModelMapper modelMapper;

    public CircuitMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CircuitDto mapTo(CircuitEntity circuitEntity) {
        return modelMapper.map(circuitEntity, CircuitDto.class);
    }

    @Override
    public CircuitEntity mapFrom(CircuitDto circuitDto) {
        return modelMapper.map(circuitDto, CircuitEntity.class);
    }
}
