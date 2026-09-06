package com.tp.F1WebSite.services;

import com.tp.F1WebSite.domain.dto.SprintResultDto;
import com.tp.F1WebSite.dto.race.ResultCreationDto;
import com.tp.F1WebSite.dto.race.SprintResultCreationDto;

import java.util.List;

public interface SprintResultService {

    List<SprintResultDto> findMany(Long raceId);

    SprintResultDto createOne(Long raceId, SprintResultCreationDto sprintResultCreationDto);

    void deleteOne(Long driverId);

    SprintResultDto updateOne(Long raceId, Long sprintResultId, SprintResultCreationDto sprintResultCreationDto);
}
