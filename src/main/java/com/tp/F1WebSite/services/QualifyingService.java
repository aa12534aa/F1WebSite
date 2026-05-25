package com.tp.F1WebSite.services;

import com.tp.F1WebSite.domain.dto.QualifyingDto;
import com.tp.F1WebSite.dto.race.QualifyingCreationDto;

import java.util.List;

public interface QualifyingService {

    List<QualifyingDto> findMany(Long raceId);

    QualifyingDto createOne(Long raceId, QualifyingCreationDto qualifyingCreationDto);

    void deleteOne(Long driverId);
}
