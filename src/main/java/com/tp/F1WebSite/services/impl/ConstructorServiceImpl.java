package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.dto.ConstructorAllInfoDto;
import com.tp.F1WebSite.dto.ConstructorRaceDto;
import com.tp.F1WebSite.dto.ConstructorWinsRacesDto;
import com.tp.F1WebSite.repositories.ConstructorRepository;
import com.tp.F1WebSite.services.ConstructorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConstructorServiceImpl implements ConstructorService {

    private ConstructorRepository constructorRepository;

    public ConstructorServiceImpl(ConstructorRepository constructorRepository) {
        this.constructorRepository = constructorRepository;
    }

    @Override
    public Boolean isExisting(Long constructorId) {
        return constructorRepository.existsById(constructorId);
    }

    @Override
    public Page<ConstructorWinsRacesDto> findManyByName(String searchName, Pageable pageable) {
        return constructorRepository.findConstructorsWinsRacesByName(searchName, pageable);
    }

    @Override
    public ConstructorAllInfoDto findOneById(Long constructorId) {
        ConstructorAllInfoDto constructorAllInfo = constructorRepository.findConstructorAllInfo(constructorId);
        List<ConstructorRaceDto> constructorRaces = constructorRepository.findConstructorAllRaces(constructorId);

        constructorAllInfo.setRaces(constructorRaces);
        return constructorAllInfo;
    }

    @Override
    public List<ConstructorWinsRacesDto> findBestConstructors() {
        return constructorRepository.findConstructorsWinsRacesByName("", PageRequest.of(0, 10)).getContent();
    }
}
