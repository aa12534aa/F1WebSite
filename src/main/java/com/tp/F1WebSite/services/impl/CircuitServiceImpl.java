package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.domain.dto.CircuitDto;
import com.tp.F1WebSite.domain.entities.CircuitEntity;
import com.tp.F1WebSite.dto.CircuitRacesDto;
import com.tp.F1WebSite.mappers.Mapper;
import com.tp.F1WebSite.repositories.CircuitRepository;
import com.tp.F1WebSite.services.CircuitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CircuitServiceImpl implements CircuitService {

    private final CircuitRepository circuitRepository;

    private final Mapper<CircuitEntity, CircuitDto> circuitMapper;

    public CircuitServiceImpl(CircuitRepository circuitRepository,
                              Mapper<CircuitEntity, CircuitDto> circuitMapper) {
        this.circuitRepository = circuitRepository;
        this.circuitMapper = circuitMapper;
    }

    @Override
    public Page<CircuitRacesDto> findManyByName(String searchCircuit, Pageable pageable) {
        return circuitRepository.findCircuitsRacesByName(searchCircuit, pageable);
    }

    @Override
    public CircuitDto createOne(CircuitDto circuitDto) {
        CircuitEntity circuitEntity = circuitMapper.mapFrom(circuitDto);

        Boolean exists = circuitRepository.existsByName(circuitEntity.getName());
        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Circuit already exists"
            );
        }

        CircuitEntity savedCircuit = circuitRepository.save(circuitEntity);

        return circuitMapper.mapTo(savedCircuit);
    }
}
