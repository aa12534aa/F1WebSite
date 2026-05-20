package com.tp.F1WebSite.controllers;

import com.tp.F1WebSite.domain.dto.DriverDto;
import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.DriverAllInfoDto;
import com.tp.F1WebSite.dto.DriverRaceDto;
import com.tp.F1WebSite.dto.DriverCircuitsWins;
import com.tp.F1WebSite.dto.DriverWinsRacesDto;
import com.tp.F1WebSite.mappers.Mapper;
import com.tp.F1WebSite.services.DriverService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(path = "/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    // GET
    @GetMapping(path = "")
    public Page<DriverWinsRacesDto> getManyDrivers(@RequestParam(required = false) String searchName, Pageable pageable) {
        Page<DriverWinsRacesDto> drivers;

        if (searchName != null && !searchName.isEmpty()) {
            drivers = driverService.findManyByName(searchName, pageable);
        } else {
            drivers = driverService.findManyByName("", pageable);
        }

        return drivers;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<DriverAllInfoDto> getOneDriver(@PathVariable("id") Long id) {
        if (!driverService.isExisting(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(driverService.findOneById(id), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/races")
    public Page<DriverRaceDto> getOneDriverRace(@PathVariable("id") Long driverId,
                                                                     @RequestParam(required = false) String searchCountry,
                                                                     Pageable pageable) {
        if (!driverService.isExisting(driverId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Page<DriverRaceDto> driverRaces;

        if (searchCountry != null && !searchCountry.isEmpty()) {
            driverRaces = driverService.findOneByIdRaces(driverId, searchCountry, pageable);
        } else {
            driverRaces = driverService.findOneByIdRaces(driverId, "", pageable);
        }

        return driverRaces;
    }

    // POST
    @PostMapping(path = "")
    public ResponseEntity<DriverDto> createDriver(@Valid @RequestBody DriverDto driverDto) {
        DriverDto savedDriver = driverService.createOne(driverDto);

        return new ResponseEntity<>(savedDriver, HttpStatus.CREATED);
    }
}
