package com.tp.F1WebSite.services;

import com.tp.F1WebSite.domain.dto.ResultDto;
import com.tp.F1WebSite.dto.ResultCreationDto;

public interface ResultService {

    ResultDto createOne(Long raceId, ResultCreationDto resultCreationDto);
}
