package com.tp.F1WebSite.controllers;

import com.tp.F1WebSite.domain.dto.CircuitDto;
import com.tp.F1WebSite.domain.dto.RaceDto;
import com.tp.F1WebSite.domain.entities.RaceEntity;
import com.tp.F1WebSite.dto.circuit.CircuitAllInfoDto;
import com.tp.F1WebSite.dto.circuit.CircuitRacesDto;
import com.tp.F1WebSite.mappers.Mapper;
import com.tp.F1WebSite.services.CircuitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/circuits")
public class CircuitController {

    private final CircuitService circuitService;


    public CircuitController(CircuitService circuitService, Mapper<RaceEntity, RaceDto> raceMapper) {
        this.circuitService = circuitService;
    }

    // GET
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

    @GetMapping(path = "/{id}")
    public ResponseEntity<CircuitAllInfoDto> getOneCircuit(@PathVariable("id") Long circuitId) {
        return new ResponseEntity<>(circuitService.findOneById(circuitId), HttpStatus.OK);
    }

    // POST
    @PostMapping(path = "")
    public ResponseEntity<CircuitDto> createCircuit(@RequestBody CircuitDto circuitDto) {
        CircuitDto savedCircuit = circuitService.createOne(circuitDto);

        return new ResponseEntity<>(savedCircuit, HttpStatus.CREATED);
    }
}
