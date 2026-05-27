package com.tp.F1WebSite.services;

import com.tp.F1WebSite.domain.dto.ConstructorDto;
import com.tp.F1WebSite.dto.constructor.ConstructorAllInfoDto;
import com.tp.F1WebSite.dto.constructor.ConstructorRaceDto;
import com.tp.F1WebSite.dto.constructor.ConstructorWinsRacesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConstructorService {

    Boolean isExisting(Long constructorId);

    Page<ConstructorWinsRacesDto> findManyByName(String searchName, Pageable pageable);

    ConstructorAllInfoDto findOneById(Long constructorId);

    Page<ConstructorRaceDto> findOneByIdRaces(Long constructorId, String searchCountry, Pageable pageable);

    List<ConstructorWinsRacesDto> findBestConstructors();

    ConstructorDto createOne(ConstructorDto constructorDto);

    void deleteOne(Long constructorId);

    ConstructorDto updateOne(Long constructorId, ConstructorDto constructorDto);
}
