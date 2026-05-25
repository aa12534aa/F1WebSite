package com.tp.F1WebSite.services;

import com.tp.F1WebSite.domain.dto.ResultDto;
import com.tp.F1WebSite.dto.race.ResultCreationDto;

import java.util.List;

public interface ResultService {

    List<ResultDto> findMany(Long raceId);

    ResultDto createOne(Long raceId, ResultCreationDto resultCreationDto);

    void deleteOne(Long driverId);
}
