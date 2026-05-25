package com.tp.F1WebSite.controllers;

import com.tp.F1WebSite.domain.dto.ConstructorDto;
import com.tp.F1WebSite.dto.constructor.ConstructorAllInfoDto;
import com.tp.F1WebSite.dto.constructor.ConstructorRaceDto;
import com.tp.F1WebSite.dto.constructor.ConstructorWinsRacesDto;
import com.tp.F1WebSite.repositories.ConstructorRepository;
import com.tp.F1WebSite.services.ConstructorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/api/constructors")
public class ConstructorController {

    private final ConstructorService constructorService;

    public ConstructorController(ConstructorService constructorService) {
        this.constructorService = constructorService;
    }

    // GET
    @GetMapping(path = "")
    public Page<ConstructorWinsRacesDto> getAllConstructors(@RequestParam(required = false) String searchName, Pageable pageable) {
        Page<ConstructorWinsRacesDto> constructors;

        if (searchName != null && !searchName.isEmpty()) {
            constructors = constructorService.findManyByName(searchName, pageable);
        } else {
            constructors = constructorService.findManyByName("", pageable);
        }

        return constructors;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ConstructorAllInfoDto> getOneConstructor(@PathVariable("id") Long constructorId) {
        return new ResponseEntity<>(constructorService.findOneById(constructorId), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/races")
    public Page<ConstructorRaceDto> getOneConstructorRace(@PathVariable("id") Long constructorId,
                                                          @RequestParam(required = false) String searchCountry,
                                                          Pageable pageable) {
        if (!constructorService.isExisting(constructorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Page<ConstructorRaceDto> constructorRaces;

        if (searchCountry != null && !searchCountry.isEmpty()) {
            constructorRaces = constructorService.findOneByIdRaces(constructorId, searchCountry, pageable);
        } else {
            constructorRaces = constructorService.findOneByIdRaces(constructorId, "", pageable);
        }

        return constructorRaces;
    }

    // POST
    @PostMapping(path = "")
    public ResponseEntity<ConstructorDto> createConstructor(@Valid @RequestBody ConstructorDto constructorDto) {
        ConstructorDto savedConstructor = constructorService.createOne(constructorDto);

        return new ResponseEntity<>(savedConstructor, HttpStatus.CREATED);
    }

    // DELETE
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteConstructor(@PathVariable("id") Long constructorId) {
        constructorService.deleteOne(constructorId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
