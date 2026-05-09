package com.tp.F1WebSite.mappers.impl;

import com.tp.F1WebSite.domain.dto.DriverDto;
import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DriverMapperImpl implements Mapper<DriverEntity, DriverDto> {

    private final ModelMapper modelMapper;

    public DriverMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public DriverDto mapTo(DriverEntity driverEntity) {
        return modelMapper.map(driverEntity, DriverDto.class);
    }

    @Override
    public DriverEntity mapFrom(DriverDto driverDto) {
        return modelMapper.map(driverDto, DriverEntity.class);
    }
}
