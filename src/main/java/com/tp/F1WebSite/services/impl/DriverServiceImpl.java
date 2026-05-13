package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.DriverAllInfoDto;
import com.tp.F1WebSite.dto.DriverCircuitsWins;
import com.tp.F1WebSite.dto.DriverRaceDto;
import com.tp.F1WebSite.dto.DriverWinsRacesDto;
import com.tp.F1WebSite.repositories.DriverRepository;
import com.tp.F1WebSite.services.DriverService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public Boolean isExisting(Long driverId) {
        return driverRepository.existsById(driverId);
    }

    @Override
    public List<DriverEntity> findAll() {
        Iterable<DriverEntity> drivers = driverRepository.findAll();
        return StreamSupport.stream(drivers.spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Page<DriverWinsRacesDto> findManyByName(String searchName, Pageable pageable) {
        Page<DriverWinsRacesDto> drivers = driverRepository.findDriversWinsRacesByName(searchName, pageable);
        return drivers;
    }

    @Override
    public DriverAllInfoDto findOneById(Long driverId) {
        DriverAllInfoDto driverAllInfo = driverRepository.findDriverAllInfo(driverId);

        Long numOfPolePositions = driverRepository.findNumOfPolePositions(driverId);
        List<DriverCircuitsWins> bestCircuits = findOneByIdBestCircuit(driverId);

        driverAllInfo.setNumOfPolePosition(numOfPolePositions);
        driverAllInfo.setBestCircuits(bestCircuits);

        return driverAllInfo;
    }

    @Override
    public Page<DriverRaceDto> findOneByIdRaces(Long driverId, String searchCountry, Pageable pageable) {
        return driverRepository.findDriverManyRacesByCountry(driverId, searchCountry, pageable);
    }

    @Override
    public List<DriverCircuitsWins> findOneByIdBestCircuit(Long driverId) {
        List<DriverCircuitsWins> driverTracksWins = driverRepository.findDriverTracksWins(driverId);
        if (driverTracksWins.isEmpty()) {
            return List.of();
        } else {
            Long numOfWins = driverTracksWins.stream().map(DriverCircuitsWins::getNumOfWins)
                    .max(Long::compareTo).orElse(0L);
            if (numOfWins > 0L) {
                return driverTracksWins.stream().filter(driverCircuitsWins ->
                        driverCircuitsWins.getNumOfWins().equals(numOfWins)).toList();
            } else {
                return List.of();
            }
        }
    }

    @Override
    public List<DriverWinsRacesDto> findBestDrivers() {
        return driverRepository.findDriversWinsRacesByName("", PageRequest.of(0, 10)).getContent();
    }
}
