package com.tp.F1WebSite.services;

import com.tp.F1WebSite.domain.dto.CircuitDto;
import com.tp.F1WebSite.dto.circuit.CircuitAllInfoDto;
import com.tp.F1WebSite.dto.circuit.CircuitDriverWinsDto;
import com.tp.F1WebSite.dto.circuit.CircuitRacesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CircuitService {

    Page<CircuitRacesDto> findManyByName(String searchCircuit, Pageable pageable);

    Boolean isExisting(Long circuitId);

    CircuitAllInfoDto findOneById(Long circuitId);

    List<CircuitDriverWinsDto> findOneByIdBestDrivers(Long circuitId);

    CircuitDto createOne(CircuitDto circuitDto);

    void deleteOne(Long circuitId);

    CircuitDto updateOne(Long circuitId, CircuitDto circuitDto);
}
