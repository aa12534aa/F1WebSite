package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.domain.dto.CircuitDto;
import com.tp.F1WebSite.domain.entities.CircuitEntity;
import com.tp.F1WebSite.domain.entities.RaceEntity;
import com.tp.F1WebSite.dto.circuit.CircuitAllInfoDto;
import com.tp.F1WebSite.dto.circuit.CircuitDriverWinsDto;
import com.tp.F1WebSite.dto.circuit.CircuitRacesDto;
import com.tp.F1WebSite.mappers.Mapper;
import com.tp.F1WebSite.repositories.CircuitRepository;
import com.tp.F1WebSite.repositories.RaceRepository;
import com.tp.F1WebSite.services.CircuitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CircuitServiceImpl implements CircuitService {

    private final CircuitRepository circuitRepository;

    private final Mapper<CircuitEntity, CircuitDto> circuitMapper;
    private final RaceRepository raceRepository;

    public CircuitServiceImpl(CircuitRepository circuitRepository,
                              Mapper<CircuitEntity, CircuitDto> circuitMapper, RaceRepository raceRepository) {
        this.circuitRepository = circuitRepository;
        this.circuitMapper = circuitMapper;
        this.raceRepository = raceRepository;
    }

    // GET
    @Override
    public Page<CircuitRacesDto> findManyByName(String searchCircuit, Pageable pageable) {
        return circuitRepository.findCircuitsRacesByName(searchCircuit, pageable);
    }

    @Override
    public Boolean isExisting(Long circuitId) {
        return circuitRepository.existsById(circuitId);
    }

    @Override
    public CircuitAllInfoDto findOneById(Long circuitId) {
        if (!circuitRepository.existsById(circuitId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Circuit does not exist"
            );
        }

        CircuitAllInfoDto circuitAllInfo = circuitRepository.findCircuitAllInfo(circuitId);

        List<CircuitDriverWinsDto> bestDrivers = findOneByIdBestDrivers(circuitId);

        circuitAllInfo.setBestDrivers(bestDrivers);

        return circuitAllInfo;
    }

    @Override
    public List<CircuitDriverWinsDto> findOneByIdBestDrivers(Long circuitId) {
        List<CircuitDriverWinsDto> circuitDriversWins = circuitRepository.findCircuitDriversWins(circuitId);
        if (circuitDriversWins.isEmpty()) {
            return List.of();
        }

        Long numOfWins = circuitDriversWins.stream().map(CircuitDriverWinsDto::getNumOfWins)
                .max(Long::compareTo).orElse(0L);
        if (numOfWins > 0L) {
            return circuitDriversWins.stream().filter(circuitDriver ->
                numOfWins.equals(circuitDriver.getNumOfWins())
            ).toList();
        }
        return List.of();
    }

    // POST
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

    // DELETE
    @Override
    public void deleteOne(Long circuitId) {
        CircuitEntity circuit = circuitRepository.findById(circuitId).
                orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Circuit does not exist"
                ));

        if (raceRepository.existsByCircuit_CircuitId(circuitId)) {
            circuit.setIsDeleted(true);
            circuitRepository.save(circuit);
        } else {
            circuitRepository.delete(circuit);
        }
    }
}
