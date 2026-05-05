package com.tp.F1WebSite.mappers.impl;

import com.tp.F1WebSite.domain.dto.ConstructorDto;
import com.tp.F1WebSite.domain.entities.ConstructorEntity;
import com.tp.F1WebSite.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ConstructorMapperImpl implements Mapper<ConstructorEntity, ConstructorDto> {

    private ModelMapper modelMapper;

    public ConstructorMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ConstructorDto mapTo(ConstructorEntity constructorEntity) {
        return modelMapper.map(constructorEntity, ConstructorDto.class);
    }

    @Override
    public ConstructorEntity mapFrom(ConstructorDto constructorDto) {
        return modelMapper.map(constructorDto, ConstructorEntity.class);
    }
}
