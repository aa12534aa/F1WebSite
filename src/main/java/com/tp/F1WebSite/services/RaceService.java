package com.tp.F1WebSite.services;

import com.tp.F1WebSite.domain.dto.RaceDto;
import com.tp.F1WebSite.dto.RaceCreationDto;
import com.tp.F1WebSite.dto.RaceCircuitDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RaceService {

    Boolean isExisting(Long raceId);

    Page<RaceCircuitDto> findManyByName(String searchName, Pageable pageable);

    RaceDto findOneById(Long raceId);

    RaceDto createOne(RaceCreationDto raceCreationDto);
}
