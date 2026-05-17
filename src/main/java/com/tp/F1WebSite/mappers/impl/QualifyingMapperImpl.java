package com.tp.F1WebSite.mappers.impl;

import com.tp.F1WebSite.domain.dto.QualifyingDto;
import com.tp.F1WebSite.domain.entities.QualifyingEntity;
import com.tp.F1WebSite.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class QualifyingMapperImpl implements Mapper<QualifyingEntity, QualifyingDto> {

    private final ModelMapper modelMapper;

    public QualifyingMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public QualifyingDto mapTo(QualifyingEntity qualifyingEntity) {
        return modelMapper.map(qualifyingEntity, QualifyingDto.class);
    }

    @Override
    public QualifyingEntity mapFrom(QualifyingDto qualifyingDto) {
        return modelMapper.map(qualifyingDto, QualifyingEntity.class);
    }
}
