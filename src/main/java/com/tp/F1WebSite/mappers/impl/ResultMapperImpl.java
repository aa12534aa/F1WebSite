package com.tp.F1WebSite.mappers.impl;

import com.tp.F1WebSite.domain.dto.ResultDto;
import com.tp.F1WebSite.domain.entities.ResultEntity;
import com.tp.F1WebSite.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ResultMapperImpl implements Mapper<ResultEntity, ResultDto> {

    private final ModelMapper modelMapper;

    public ResultMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ResultDto mapTo(ResultEntity resultEntity) {
        return modelMapper.map(resultEntity, ResultDto.class);
    }

    @Override
    public ResultEntity mapFrom(ResultDto resultDto) {
        return modelMapper.map(resultDto, ResultEntity.class);
    }
}
