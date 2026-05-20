package com.tp.F1WebSite.services;

import com.tp.F1WebSite.domain.dto.CircuitDto;
import com.tp.F1WebSite.dto.CircuitAllInfoDto;
import com.tp.F1WebSite.dto.CircuitDriverWinsDto;
import com.tp.F1WebSite.dto.CircuitRacesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CircuitService {

    Page<CircuitRacesDto> findManyByName(String searchCircuit, Pageable pageable);

    Boolean isExisting(Long circuitId);

    CircuitAllInfoDto findOneById(Long circuitId);

    List<CircuitDriverWinsDto> findOneByIdBestDrivers(Long circuitId);

    CircuitDto createOne(CircuitDto circuitDto);
}
