package com.tp.F1WebSite.services;

import com.tp.F1WebSite.domain.dto.DriverDto;
import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.driver.DriverAllInfoDto;
import com.tp.F1WebSite.dto.driver.DriverRaceDto;
import com.tp.F1WebSite.dto.driver.DriverCircuitsWins;
import com.tp.F1WebSite.dto.driver.DriverWinsRacesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DriverService {

    Boolean isExisting(Long driverId);

    List<DriverEntity> findAll();

    Page<DriverWinsRacesDto> findManyByName(String searchName, Pageable pageable);

    DriverAllInfoDto findOneById(Long driverId);

    Page<DriverRaceDto> findOneByIdRaces(Long driverId, String searchCountry, Pageable pageable);

    List<DriverCircuitsWins> findOneByIdBestCircuit(Long driverId);

    List<DriverWinsRacesDto> findBestDrivers();

    DriverDto createOne(DriverDto driverDto);

    void deleteOne(Long driverId);
}
