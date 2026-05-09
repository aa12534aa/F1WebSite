package com.tp.F1WebSite.controllers;

import com.tp.F1WebSite.domain.dto.DriverDto;
import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.DriverAllInfoDto;
import com.tp.F1WebSite.dto.DriverWinsRacesDto;
import com.tp.F1WebSite.mappers.Mapper;
import com.tp.F1WebSite.services.DriverService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DriverController {

    private final DriverService driverService;

    private final Mapper<DriverEntity, DriverDto> driverMapper;

    public DriverController(DriverService driverService, Mapper<DriverEntity, DriverDto> driverMapper) {
        this.driverService = driverService;
        this.driverMapper = driverMapper;
    }

    @GetMapping(path = "/drivers")
    public Page<DriverWinsRacesDto> getManyDrivers(@RequestParam(required = false) String searchName, Pageable pageable) {
        Page<DriverWinsRacesDto> drivers;

        if (searchName != null && !searchName.isEmpty()) {
            drivers = driverService.findManyByName(searchName, pageable);
        } else {
            drivers = driverService.findManyByName("", pageable);
        }

        return drivers;
    }

    @GetMapping(path = "/drivers/{id}")
    public ResponseEntity<DriverAllInfoDto> getOneDriver(@PathVariable("id") Long id) {
        if (!driverService.isExisting(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        DriverAllInfoDto driverAllInfo = driverService.findOneById(id);
        return new ResponseEntity<>(driverAllInfo, HttpStatus.OK);
    }
}
