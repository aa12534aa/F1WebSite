package com.tp.F1WebSite.services;

import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.DriverAllInfoDto;
import com.tp.F1WebSite.dto.DriverWinsRacesDto;

import java.util.List;

public interface DriverService {

    Boolean isExisting(Long id);

    List<DriverEntity> findAll();

    List<DriverWinsRacesDto> findManyByName(String searchName);

    DriverAllInfoDto findOneById(Long id);
}
