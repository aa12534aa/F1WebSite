package com.tp.F1WebSite.mappers.impl;

import com.tp.F1WebSite.domain.dto.SprintResultDto;
import com.tp.F1WebSite.domain.entities.SprintResultEntity;
import com.tp.F1WebSite.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class SprintResultMapperImpl implements Mapper<SprintResultEntity, SprintResultDto> {
    private final ModelMapper modelMapper;

    public SprintResultMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public SprintResultDto mapTo(SprintResultEntity sprintResultEntity) {
        return modelMapper.map(sprintResultEntity, SprintResultDto.class);
    }

    @Override
    public SprintResultEntity mapFrom(SprintResultDto sprintResultDto) {
        return modelMapper.map(sprintResultDto, SprintResultEntity.class);
    }
}

