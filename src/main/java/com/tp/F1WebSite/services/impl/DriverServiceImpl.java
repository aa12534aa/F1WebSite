package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.DriverAllInfoDto;
import com.tp.F1WebSite.dto.DriverRacesDto;
import com.tp.F1WebSite.dto.DriverWinsRacesDto;
import com.tp.F1WebSite.repositories.DriverRepository;
import com.tp.F1WebSite.services.DriverService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DriverServiceImpl implements DriverService {

    private DriverRepository driverRepository;

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
    public List<DriverWinsRacesDto> findManyByName(String searchName) {
        List<DriverWinsRacesDto> drivers = driverRepository.findDriversWinsRacesByName(searchName);
        return drivers;
    }

    @Override
    public DriverAllInfoDto findOneById(Long driverId) {
        DriverAllInfoDto driverAllInfo = driverRepository.findDriverAllInfo(driverId);
        List<DriverRacesDto> driverRaces = driverRepository.findDriverAllRaces(driverId);

        driverAllInfo.setRaces(driverRaces);

        return driverAllInfo;
    }
}
