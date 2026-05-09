package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.DriverAllInfoDto;
import com.tp.F1WebSite.dto.DriverTrackWins;
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

        List<DriverRaceDto> driverRaces = driverRepository.findDriverAllRaces(driverId);

        Long numOfPolePositions = driverRepository.findNumOfPolePositions(driverId);

        List<DriverTrackWins> driverTracksWins = driverRepository.findDriverTracksWins(driverId);
        if (driverTracksWins.isEmpty()) {
            driverAllInfo.setBestTracks(List.of());
        } else {
            Long numOfWins = driverTracksWins.stream().map(DriverTrackWins::getNumOfWins)
                    .max(Long::compareTo).orElse(0L);
            if (numOfWins > 0L) {
                List<DriverTrackWins> bestTracks = driverTracksWins.stream().filter(driverTrackWins ->
                        driverTrackWins.getNumOfWins().
                        equals(numOfWins)).toList();
                driverAllInfo.setBestTracks(bestTracks);
            } else {
                driverAllInfo.setBestTracks(List.of());
            }
        }

        driverAllInfo.setRaces(driverRaces);
        driverAllInfo.setNumOfPolePosition(numOfPolePositions);

        return driverAllInfo;
    }

    @Override
    public List<DriverWinsRacesDto> findBestDrivers() {
        return driverRepository.findDriversWinsRacesByName("", PageRequest.of(0, 10)).getContent();
    }
}
