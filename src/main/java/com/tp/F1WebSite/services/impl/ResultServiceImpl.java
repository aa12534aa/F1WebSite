package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.domain.dto.ResultDto;
import com.tp.F1WebSite.domain.entities.ConstructorEntity;
import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.domain.entities.RaceEntity;
import com.tp.F1WebSite.domain.entities.ResultEntity;
import com.tp.F1WebSite.dto.race.ResultCreationDto;
import com.tp.F1WebSite.mappers.Mapper;
import com.tp.F1WebSite.repositories.ConstructorRepository;
import com.tp.F1WebSite.repositories.DriverRepository;
import com.tp.F1WebSite.repositories.RaceRepository;
import com.tp.F1WebSite.repositories.ResultRepository;
import com.tp.F1WebSite.services.ResultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;
    private final RaceRepository raceRepository;
    private final DriverRepository driverRepository;
    private final ConstructorRepository constructorRepository;

    private final Mapper<ResultEntity, ResultDto> resultMapper;

    public ResultServiceImpl(ResultRepository resultRepository,
                             RaceRepository raceRepository,
                             DriverRepository driverRepository,
                             ConstructorRepository constructorRepository,
                             Mapper<ResultEntity, ResultDto> resultMapper) {
        this.resultRepository = resultRepository;
        this.raceRepository = raceRepository;
        this.driverRepository = driverRepository;
        this.constructorRepository = constructorRepository;
        this.resultMapper = resultMapper;
    }

    // GET
    @Override
    public List<ResultDto> findMany(Long raceId) {
        if (!raceRepository.existsById(raceId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Race does not exist"
            );
        }

        List<ResultEntity> resultsEntity = resultRepository.findAllByRace_RaceIdOrderByPositionAsc(raceId);

        return resultsEntity.stream().map(resultMapper::mapTo).collect(Collectors.toList());
    }

    // POST
    @Override
    public ResultDto createOne(Long raceId, ResultCreationDto resultCreationDto) {

        if (resultRepository.existsByRace_RaceIdAndDriver_Url(raceId, resultCreationDto.getDriverUrl())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Result already exists"
            );
        }

        RaceEntity raceEntity = raceRepository.findById(raceId).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Race does not exist"
                )
        );

        DriverEntity driverEntity = driverRepository
                .findByUrl(resultCreationDto.getDriverUrl()).orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Driver does not exist"
                )
        );

        ConstructorEntity constructorEntity = constructorRepository
                .findByName(resultCreationDto.getConstructorName()).orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Constructor does not exist"
                )
        );

        ResultEntity resultEntity = ResultEntity.builder()
                .race(raceEntity)
                .driver(driverEntity)
                .constructor(constructorEntity)
                .grid(resultCreationDto.getGrid())
                .position(resultCreationDto.getPosition())
                .points(resultCreationDto.getPoints())
                .build();
        ResultEntity savedResult = resultRepository.save(resultEntity);
        return resultMapper.mapTo(savedResult);
    }
}
