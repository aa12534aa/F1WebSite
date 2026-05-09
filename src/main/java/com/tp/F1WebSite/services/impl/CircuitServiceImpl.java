package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.dto.CircuitRacesDto;
import com.tp.F1WebSite.repositories.CircuitRepository;
import com.tp.F1WebSite.services.CircuitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CircuitServiceImpl implements CircuitService {

    private final CircuitRepository circuitRepository;

    public CircuitServiceImpl(CircuitRepository circuitRepository) {
        this.circuitRepository = circuitRepository;
    }

    @Override
    public Page<CircuitRacesDto> findManyByName(String searchCircuit, Pageable pageable) {
        return circuitRepository.findTracksRacesByName(searchCircuit, pageable);
    }
}
