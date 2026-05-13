package com.tp.F1WebSite.controllers;

import com.tp.F1WebSite.domain.dto.RaceDto;
import com.tp.F1WebSite.domain.entities.RaceEntity;
import com.tp.F1WebSite.dto.CircuitRacesDto;
import com.tp.F1WebSite.mappers.Mapper;
import com.tp.F1WebSite.services.CircuitService;
import com.tp.F1WebSite.services.RaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/circuits")
public class CircuitController {

    private final CircuitService circuitService;


    public CircuitController(CircuitService circuitService, Mapper<RaceEntity, RaceDto> raceMapper) {
        this.circuitService = circuitService;
    }

    @GetMapping(path = "")
    public Page<CircuitRacesDto> getManyTracks(@RequestParam(required = false) String searchCircuit, Pageable pageable) {
        Page<CircuitRacesDto> tracks;

        if (searchCircuit != null && !searchCircuit.isEmpty()) {
            tracks = circuitService.findManyByName(searchCircuit, pageable);
        } else {
            tracks = circuitService.findManyByName("", pageable);
        }

        return tracks;
    }
}
