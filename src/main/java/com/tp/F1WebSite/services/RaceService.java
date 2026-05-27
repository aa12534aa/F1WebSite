package com.tp.F1WebSite.services;

import com.tp.F1WebSite.domain.dto.RaceDto;
import com.tp.F1WebSite.dto.race.RaceCreationDto;
import com.tp.F1WebSite.dto.race.RaceCircuitDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RaceService {

    Boolean isExisting(Long raceId);

    Page<RaceCircuitDto> findManyByName(String searchName, Pageable pageable);

    RaceDto findOneById(Long raceId);

    RaceDto createOne(RaceCreationDto raceCreationDto);

    void deleteOne(Long raceId);

    RaceDto updateOne(Long raceId, RaceCreationDto raceCreationDto);
}
