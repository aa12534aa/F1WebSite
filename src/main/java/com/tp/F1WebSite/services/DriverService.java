package com.tp.F1WebSite.services;

import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.DriverAllInfoDto;
import com.tp.F1WebSite.dto.DriverRaceDto;
import com.tp.F1WebSite.dto.DriverCircuitsWins;
import com.tp.F1WebSite.dto.DriverWinsRacesDto;
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
}
