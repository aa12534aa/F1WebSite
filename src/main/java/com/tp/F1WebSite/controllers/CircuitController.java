package com.tp.F1WebSite.controllers;

import com.tp.F1WebSite.domain.dto.CircuitDto;
import com.tp.F1WebSite.dto.circuit.CircuitAllInfoDto;
import com.tp.F1WebSite.dto.circuit.CircuitRacesDto;
import com.tp.F1WebSite.services.CircuitService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/circuits")
public class CircuitController {

    private final CircuitService circuitService;


    public CircuitController(CircuitService circuitService) {
        this.circuitService = circuitService;
    }

    // GET
    @GetMapping(path = "")
    public Page<CircuitRacesDto> getManyTracks(@RequestParam(required = false) String searchName, Pageable pageable) {
        Page<CircuitRacesDto> tracks;

        if (searchName != null && !searchName.isEmpty()) {
            tracks = circuitService.findManyByName(searchName, pageable);
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

    // DELETE
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteCircuit(@PathVariable("id") Long circuitId) {
        circuitService.deleteOne(circuitId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // PUT
    @PutMapping(path = "/{id}")
    public ResponseEntity<CircuitDto> updateCircuit(@PathVariable("id") Long circuitId,
                                                    @Valid @RequestBody CircuitDto circuitDto) {
        CircuitDto updatedCircuit = circuitService.updateOne(circuitId, circuitDto);

        return new ResponseEntity<>(updatedCircuit, HttpStatus.CREATED);
    }
}
