package com.tp.F1WebSite.controllers;

import com.tp.F1WebSite.domain.dto.DriverDto;
import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.mappers.Mapper;
import com.tp.F1WebSite.services.DriverService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DriverController {

    private DriverService driverService;

    private Mapper<DriverEntity, DriverDto> driverMapper;

    public DriverController(DriverService driverService, Mapper<DriverEntity, DriverDto> driverMapper) {
        this.driverService = driverService;
        this.driverMapper = driverMapper;
    }

    @GetMapping(path = "/drivers")
    public List<DriverDto> getAllDrivers(@RequestBody(required = false) String searchName) {
        List<DriverEntity> drivers;

        if (searchName != null && !searchName.isEmpty()) {
            drivers = driverService.findManyByName(searchName);
        } else {
            drivers = driverService.findAll();
        }

        return drivers.stream().map(driverMapper::mapTo).collect(Collectors.toList());
    }
}
