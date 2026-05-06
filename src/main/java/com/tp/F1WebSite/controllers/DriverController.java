package com.tp.F1WebSite.controllers;

import com.tp.F1WebSite.domain.dto.DriverDto;
import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.DriverAllInfoDto;
import com.tp.F1WebSite.dto.DriverWinsRacesDto;
import com.tp.F1WebSite.mappers.Mapper;
import com.tp.F1WebSite.services.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DriverController {

    private DriverService driverService;

    private Mapper<DriverEntity, DriverDto> driverMapper;

    public DriverController(DriverService driverService, Mapper<DriverEntity, DriverDto> driverMapper) {
        this.driverService = driverService;
        this.driverMapper = driverMapper;
    }

    @GetMapping(path = "/drivers")
    public List<DriverWinsRacesDto> getManyDrivers(@RequestParam(required = false) String searchName) {
        List<DriverWinsRacesDto> drivers;
        System.out.println(searchName);

        if (searchName != null && !searchName.isEmpty()) {
            drivers = driverService.findManyByName(searchName);
        } else {
            drivers = driverService.findManyByName("");
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
