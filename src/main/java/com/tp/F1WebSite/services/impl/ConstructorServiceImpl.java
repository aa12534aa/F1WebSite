package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.domain.dto.ConstructorDto;
import com.tp.F1WebSite.domain.entities.ConstructorEntity;
import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.constructor.ConstructorAllInfoDto;
import com.tp.F1WebSite.dto.constructor.ConstructorRaceDto;
import com.tp.F1WebSite.dto.constructor.ConstructorWinsRacesDto;
import com.tp.F1WebSite.mappers.Mapper;
import com.tp.F1WebSite.repositories.ConstructorRepository;
import com.tp.F1WebSite.repositories.QualifyingRepository;
import com.tp.F1WebSite.repositories.ResultRepository;
import com.tp.F1WebSite.services.ConstructorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ConstructorServiceImpl implements ConstructorService {

    private final ConstructorRepository constructorRepository;

    private final Mapper<ConstructorEntity, ConstructorDto> constructorMapper;
    private final ResultRepository resultRepository;
    private final QualifyingRepository qualifyingRepository;

    public ConstructorServiceImpl(ConstructorRepository constructorRepository,
                                  Mapper<ConstructorEntity, ConstructorDto> constructorMapper, ResultRepository resultRepository, QualifyingRepository qualifyingRepository) {
        this.constructorRepository = constructorRepository;
        this.constructorMapper = constructorMapper;
        this.resultRepository = resultRepository;
        this.qualifyingRepository = qualifyingRepository;
    }

    // GET
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
        if (!constructorRepository.existsById(constructorId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Constructor does not exist"
            );
        }


        return constructorRepository.findConstructorAllInfo(constructorId);
    }

    @Override
    public Page<ConstructorRaceDto> findOneByIdRaces(Long constructorId, String searchCountry, Pageable pageable) {
        if (!constructorRepository.existsById(constructorId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Constructor does not exist"
            );
        }


        return constructorRepository.findConstructorAllRaces(constructorId, searchCountry, pageable);
    }

    @Override
    public List<ConstructorWinsRacesDto> findBestConstructors() {
        return constructorRepository.findConstructorsWinsRacesByName("", PageRequest.of(0, 10)).getContent();
    }

    // POST
    @Override
    public ConstructorDto createOne(ConstructorDto constructorDto) {
        ConstructorEntity constructorEntity = constructorMapper.mapFrom(constructorDto);

        Boolean exists = constructorRepository.existsByName(constructorEntity.getName());
        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Constructor already exists"
            );
        }

        ConstructorEntity savedConstructor = constructorRepository.save(constructorEntity);

        return constructorMapper.mapTo(savedConstructor);
    }

    // DELETE
    @Override
    public void deleteOne(Long constructorId) {
        ConstructorEntity constructor = constructorRepository.findById(constructorId).
                orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Constructor does not exist"
                ));

        if (resultRepository.existsByConstructor_ConstructorId(constructorId) ||
                qualifyingRepository.existsByConstructor_ConstructorId(constructorId)) {
            constructor.setIsDeleted(true);
            constructorRepository.save(constructor);
        } else {
            constructorRepository.delete(constructor);
        }
    }

    // PUT
    @Override
    public ConstructorDto updateOne(Long constructorId, ConstructorDto constructorDto) {
        ConstructorEntity constructorEntity = constructorRepository.findById(constructorId).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Constructor does not exist"
                )
        );

        if (!constructorEntity.getName().equals(constructorDto.getName()) &&
                constructorRepository.existsByName(constructorDto.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Driver already exists"
            );
        }

        ConstructorEntity constructorToUpdate = constructorMapper.mapFrom(constructorDto);
        constructorToUpdate.setConstructorId(constructorId);

        ConstructorEntity updatedConstructor = constructorRepository.save(constructorToUpdate);
        return constructorMapper.mapTo(updatedConstructor);
    }
}
