package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.domain.dto.QualifyingDto;
import com.tp.F1WebSite.domain.entities.ConstructorEntity;
import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.domain.entities.QualifyingEntity;
import com.tp.F1WebSite.domain.entities.RaceEntity;
import com.tp.F1WebSite.dto.QualifyingCreationDto;
import com.tp.F1WebSite.mappers.Mapper;
import com.tp.F1WebSite.repositories.ConstructorRepository;
import com.tp.F1WebSite.repositories.DriverRepository;
import com.tp.F1WebSite.repositories.QualifyingRepository;
import com.tp.F1WebSite.repositories.RaceRepository;
import com.tp.F1WebSite.services.QualifyingService;
import com.tp.F1WebSite.services.RaceService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QualifyingServiceImpl implements QualifyingService {

    private final RaceRepository raceRepository;
    private final QualifyingRepository qualifyingRepository;
    private final DriverRepository driverRepository;
    private final ConstructorRepository constructorRepository;

    private final Mapper<QualifyingEntity, QualifyingDto> qualifyingMapper;

    public QualifyingServiceImpl(QualifyingRepository qualifyingRepository,
                                 RaceService raceService,
                                 RaceRepository raceRepository,
                                 DriverRepository driverRepository,
                                 ConstructorRepository constructorRepository,
                                 Mapper<QualifyingEntity, QualifyingDto> qualifyingMapper) {
        this.qualifyingRepository = qualifyingRepository;
        this.raceRepository = raceRepository;
        this.driverRepository = driverRepository;
        this.constructorRepository = constructorRepository;
        this.qualifyingMapper = qualifyingMapper;
    }

    @Override
    public QualifyingDto createOne(Long raceId, QualifyingCreationDto qualifyingCreationDto) {

        if (qualifyingRepository.existsByRace_RaceIdAndDriver_Url(raceId, qualifyingCreationDto.getDriverUrl())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Qualifying already exists"
            );
        }

        RaceEntity raceEntity = raceRepository.findById(raceId).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Race does not exist"
                )
        );

        DriverEntity driverEntity = driverRepository.findByUrl(qualifyingCreationDto.getDriverUrl()).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Driver does not exist"
                )
        );

        ConstructorEntity constructorEntity = constructorRepository.findByName(qualifyingCreationDto.getConstructorName()).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Constructor does not exist"
                )
        );

        QualifyingEntity qualifyingEntity = QualifyingEntity.builder()
                .race(raceEntity)
                .driver(driverEntity)
                .constructor(constructorEntity)
                .position(qualifyingCreationDto.getPosition())
                .build();

        QualifyingEntity savedQualifying = qualifyingRepository.save(qualifyingEntity);
        return qualifyingMapper.mapTo(savedQualifying);
    }
}
