package com.tp.F1WebSite.services;

import com.tp.F1WebSite.dto.CircuitRacesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CircuitService {

    Page<CircuitRacesDto> findManyByName(String searchCircuit, Pageable pageable);
}
