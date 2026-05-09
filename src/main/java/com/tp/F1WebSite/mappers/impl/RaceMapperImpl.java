package com.tp.F1WebSite.mappers.impl;

import com.tp.F1WebSite.domain.dto.RaceDto;
import com.tp.F1WebSite.domain.entities.RaceEntity;
import com.tp.F1WebSite.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RaceMapperImpl implements Mapper<RaceEntity, RaceDto> {

    private final ModelMapper modelMapper;

    public RaceMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public RaceDto mapTo(RaceEntity raceEntity) {
        return modelMapper.map(raceEntity, RaceDto.class);
    }

    @Override
    public RaceEntity mapFrom(RaceDto raceDto) {
        return modelMapper.map(raceDto, RaceEntity.class);
    }
}
