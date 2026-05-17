package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.domain.dto.RaceDto;
import com.tp.F1WebSite.domain.entities.CircuitEntity;
import com.tp.F1WebSite.domain.entities.RaceEntity;
import com.tp.F1WebSite.dto.RaceCreationDto;
import com.tp.F1WebSite.dto.RaceCircuitDto;
import com.tp.F1WebSite.mappers.Mapper;
import com.tp.F1WebSite.repositories.CircuitRepository;
import com.tp.F1WebSite.repositories.RaceRepository;
import com.tp.F1WebSite.services.RaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RaceServiceImpl implements RaceService {

    private final RaceRepository raceRepository;

    private final CircuitRepository circuitRepository;

    private final Mapper<RaceEntity, RaceDto> raceMapper;

    public RaceServiceImpl(RaceRepository raceRepository, CircuitRepository circuitRepository,
                           Mapper<RaceEntity, RaceDto> raceMapper) {
        this.raceRepository = raceRepository;
        this.circuitRepository = circuitRepository;
        this.raceMapper = raceMapper;
    }

    @Override
    public Boolean isExisting(Long raceId) {
        return raceRepository.existsById(raceId);
    }

    @Override
    public Page<RaceCircuitDto> findManyByName(String searchName, Pageable pageable) {
        return raceRepository.findRacesCircuitsByName(searchName, pageable);
    }

    @Override
    public RaceDto findOneById(Long raceId) {
        RaceEntity raceEntity = raceRepository.findById(raceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Race Not Found"
        ));
        return raceMapper.mapTo(raceEntity);
    }

    @Override
    public RaceDto createOne(RaceCreationDto raceCreationDto) {
        if (raceRepository.existsByDate(raceCreationDto.getDate())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Race with this date already exists"
            );
        }

        CircuitEntity circuit = circuitRepository.findByName(raceCreationDto.getCircuitName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Circuit does not exist"
                ));
        RaceEntity raceEntity = RaceEntity.builder()
                .circuit(circuit)
                .date(raceCreationDto.getDate())
                .name(raceCreationDto.getName())
                .build();
        RaceEntity savedRace = raceRepository.save(raceEntity);
        return raceMapper.mapTo(savedRace);
    }
}
