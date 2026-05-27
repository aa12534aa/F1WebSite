package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.domain.dto.RaceDto;
import com.tp.F1WebSite.domain.entities.CircuitEntity;
import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.domain.entities.RaceEntity;
import com.tp.F1WebSite.dto.race.RaceCreationDto;
import com.tp.F1WebSite.dto.race.RaceCircuitDto;
import com.tp.F1WebSite.mappers.Mapper;
import com.tp.F1WebSite.repositories.CircuitRepository;
import com.tp.F1WebSite.repositories.QualifyingRepository;
import com.tp.F1WebSite.repositories.RaceRepository;
import com.tp.F1WebSite.repositories.ResultRepository;
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
    private final ResultRepository resultRepository;
    private final QualifyingRepository qualifyingRepository;

    public RaceServiceImpl(RaceRepository raceRepository, CircuitRepository circuitRepository,
                           Mapper<RaceEntity, RaceDto> raceMapper, ResultRepository resultRepository, QualifyingRepository qualifyingRepository) {
        this.raceRepository = raceRepository;
        this.circuitRepository = circuitRepository;
        this.raceMapper = raceMapper;
        this.resultRepository = resultRepository;
        this.qualifyingRepository = qualifyingRepository;
    }

    // GET
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

    // POST
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
                .isDeleted(false)
                .build();
        RaceEntity savedRace = raceRepository.save(raceEntity);
        return raceMapper.mapTo(savedRace);
    }

    // DELETE
    @Override
    public void deleteOne(Long raceId) {
        RaceEntity race = raceRepository.findById(raceId).
                orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Race does not exist"
                ));

        if (resultRepository.existsByRace_RaceId(raceId) ||
                qualifyingRepository.existsByRace_RaceId(raceId)) {
            race.setIsDeleted(true);
            raceRepository.save(race);
        } else {
            raceRepository.delete(race);
        }
    }

    // PUT
    @Override
    public RaceDto updateOne(Long raceId, RaceCreationDto raceCreationDto) {
        RaceEntity raceEntity = raceRepository.findById(raceId).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Race does not exist"
                )
        );

        if (!raceEntity.getDate().equals(raceCreationDto.getDate()) &&
                raceRepository.existsByDate(raceCreationDto.getDate())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Race already exists"
            );
        }

        CircuitEntity circuitEntity = circuitRepository.findByName(raceCreationDto.getCircuitName()).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Circuit does not exist"
                )
        );

        RaceEntity raceToUpdate = RaceEntity.builder()
                .raceId(raceId)
                .circuit(circuitEntity)
                .date(raceCreationDto.getDate())
                .name(raceCreationDto.getName())
                .isDeleted(false)
                .build();

        RaceEntity updatedRace = raceRepository.save(raceToUpdate);
        return raceMapper.mapTo(updatedRace);
    }
}
