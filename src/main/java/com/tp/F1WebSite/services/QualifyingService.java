package com.tp.F1WebSite.services;

import com.tp.F1WebSite.domain.dto.QualifyingDto;
import com.tp.F1WebSite.dto.QualifyingCreationDto;

public interface QualifyingService {

    QualifyingDto createOne(Long raceId, QualifyingCreationDto qualifyingCreationDto);
}
