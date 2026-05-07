package com.tp.F1WebSite.controllers;

import com.tp.F1WebSite.domain.dto.ConstructorDto;
import com.tp.F1WebSite.domain.entities.ConstructorEntity;
import com.tp.F1WebSite.dto.ConstructorAllInfoDto;
import com.tp.F1WebSite.dto.ConstructorWinsRacesDto;
import com.tp.F1WebSite.mappers.Mapper;
import com.tp.F1WebSite.services.ConstructorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConstructorController {

    private ConstructorService constructorService;

    private Mapper<ConstructorEntity, ConstructorDto> constructorMapper;

    public ConstructorController(ConstructorService constructorService, Mapper<ConstructorEntity, ConstructorDto> constructorMapper) {
        this.constructorService = constructorService;
        this.constructorMapper = constructorMapper;
    }

    @GetMapping("/constructors")
    public Page<ConstructorWinsRacesDto> getAllConstructors(@RequestParam(required = false) String searchName, Pageable pageable) {
        Page<ConstructorWinsRacesDto> constructors;

        if (searchName != null && !searchName.isEmpty()) {
            constructors = constructorService.findManyByName(searchName, pageable);
        } else {
            constructors = constructorService.findManyByName("", pageable);
        }

        return constructors;
    }

    @GetMapping("/constructors/{id}")
    public ResponseEntity<ConstructorAllInfoDto> getOneConstructor(@PathVariable("id") Long id) {
        if (!constructorService.isExisting(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(constructorService.findOneById(id), HttpStatus.OK);
    }
}
