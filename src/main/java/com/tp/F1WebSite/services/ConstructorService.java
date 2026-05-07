package com.tp.F1WebSite.services;

import com.tp.F1WebSite.dto.ConstructorAllInfoDto;
import com.tp.F1WebSite.dto.ConstructorRaceDto;
import com.tp.F1WebSite.dto.ConstructorWinsRacesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConstructorService {

    Boolean isExisting(Long constructorId);

    Page<ConstructorWinsRacesDto> findManyByName(String searchName, Pageable pageable);

    ConstructorAllInfoDto findOneById(Long constructorId);

    List<ConstructorWinsRacesDto> findBestConstructors();
}
