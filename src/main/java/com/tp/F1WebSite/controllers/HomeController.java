package com.tp.F1WebSite.controllers;

import com.tp.F1WebSite.domain.dto.DriverDto;
import com.tp.F1WebSite.dto.BestDriversConstructors;
import com.tp.F1WebSite.dto.ConstructorWinsRacesDto;
import com.tp.F1WebSite.dto.DriverWinsRacesDto;
import com.tp.F1WebSite.services.ConstructorService;
import com.tp.F1WebSite.services.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final DriverService driverService;
    private final ConstructorService constructorService;

    public HomeController(DriverService driverService, ConstructorService constructorService) {
        this.driverService = driverService;
        this.constructorService = constructorService;
    }

    @GetMapping(path = "")
    public ResponseEntity<BestDriversConstructors> getBestAndDriversConstructors() {
        List<DriverWinsRacesDto> bestDrivers = driverService.findBestDrivers();
        List<ConstructorWinsRacesDto> bestConstructors = constructorService.findBestConstructors();

        BestDriversConstructors bestDriversConstructors = new BestDriversConstructors();
        bestDriversConstructors.setBestDrivers(bestDrivers);
        bestDriversConstructors.setBestConstructors(bestConstructors);

        return new ResponseEntity<>(bestDriversConstructors, HttpStatus.OK);
    }
}
