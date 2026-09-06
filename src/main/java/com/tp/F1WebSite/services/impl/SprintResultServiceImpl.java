package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.domain.dto.SprintResultDto;
import com.tp.F1WebSite.domain.entities.*;
import com.tp.F1WebSite.dto.race.SprintResultCreationDto;
import com.tp.F1WebSite.mappers.Mapper;
import com.tp.F1WebSite.repositories.*;
import com.tp.F1WebSite.services.SprintResultService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SprintResultServiceImpl implements SprintResultService {

    private final SprintResultRepository sprintResultRepository;
    private final RaceRepository raceRepository;
    private final DriverRepository driverRepository;
    private final ConstructorRepository constructorRepository;

    private final Mapper<SprintResultEntity, SprintResultDto> sprintResultMapper;

    public SprintResultServiceImpl(SprintResultRepository sprintResultRepository,
                             RaceRepository raceRepository,
                             DriverRepository driverRepository,
                             ConstructorRepository constructorRepository,
                           Mapper<SprintResultEntity, SprintResultDto> sprintResultMapper) {
        this.sprintResultRepository = sprintResultRepository;
        this.raceRepository = raceRepository;
        this.driverRepository = driverRepository;
        this.constructorRepository = constructorRepository;
        this.sprintResultMapper = sprintResultMapper;
    }

    // GET
    @Override
    public List<SprintResultDto> findMany(Long raceId) {
        if (!raceRepository.existsById(raceId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Race does not exist"
            );
        }

        List<SprintResultEntity> resultsEntity = sprintResultRepository.findAllByRace_RaceIdOrderByPositionAsc(raceId);

        return resultsEntity.stream().map(sprintResultMapper::mapTo).collect(Collectors.toList());
    }

    // POST
    @Override
    public SprintResultDto createOne(Long raceId, SprintResultCreationDto sprintResultCreationDto) {
        if (sprintResultRepository.existsByRace_RaceIdAndDriver_Url(raceId, sprintResultCreationDto.getDriverUrl())) {
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
                .findByUrl(sprintResultCreationDto.getDriverUrl()).orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Driver does not exist"
                        )
                );

        ConstructorEntity constructorEntity = constructorRepository
                .findByName(sprintResultCreationDto.getConstructorName()).orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Constructor does not exist"
                        )
                );

        SprintResultEntity sprintResultEntity = SprintResultEntity.builder()
                .race(raceEntity)
                .driver(driverEntity)
                .constructor(constructorEntity)
                .grid(sprintResultCreationDto.getGrid())
                .position(sprintResultCreationDto.getPosition())
                .points(sprintResultCreationDto.getPoints())
                .build();
        SprintResultEntity savedResult = sprintResultRepository.save(sprintResultEntity);
        return sprintResultMapper.mapTo(savedResult);
    }

    // DELETE
    @Override
    public void deleteOne(Long sprintResultId) {
        if (!sprintResultRepository.existsById(sprintResultId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Result does not exist"
            );
        }

        sprintResultRepository.deleteById(sprintResultId);
    }

    // PUT
    @Override
    public SprintResultDto updateOne(Long raceId, Long sprintResultId, SprintResultCreationDto sprintResultCreationDto) {
        SprintResultEntity sprintResultEntity = sprintResultRepository.findById(sprintResultId).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Result does not exist"
                )
        );

        if (!sprintResultEntity.getDriver().getUrl().equals(sprintResultCreationDto.getDriverUrl()) &&
                sprintResultRepository.existsByDriver_UrlAndRace_RaceId(sprintResultCreationDto.getDriverUrl(), raceId)) {
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
                .findByUrl(sprintResultCreationDto.getDriverUrl()).orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Driver does not exist"
                        )
                );

        ConstructorEntity constructorEntity = constructorRepository
                .findByName(sprintResultCreationDto.getConstructorName()).orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Constructor does not exist"
                        )
                );

        SprintResultEntity sprintResultToUpdate = SprintResultEntity.builder()
                .resultId(sprintResultId)
                .race(raceEntity)
                .driver(driverEntity)
                .constructor(constructorEntity)
                .grid(sprintResultCreationDto.getGrid())
                .position(sprintResultCreationDto.getPosition())
                .points(sprintResultCreationDto.getPoints())
                .build();

        SprintResultEntity updatedResult = sprintResultRepository.save(sprintResultToUpdate);
        return sprintResultMapper.mapTo(updatedResult);
    }
}
