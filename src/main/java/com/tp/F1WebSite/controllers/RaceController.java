package com.tp.F1WebSite.controllers;

import com.tp.F1WebSite.domain.dto.QualifyingDto;
import com.tp.F1WebSite.domain.dto.RaceDto;
import com.tp.F1WebSite.domain.dto.ResultDto;
import com.tp.F1WebSite.dto.race.QualifyingCreationDto;
import com.tp.F1WebSite.dto.race.RaceCreationDto;
import com.tp.F1WebSite.dto.race.RaceCircuitDto;
import com.tp.F1WebSite.dto.race.ResultCreationDto;
import com.tp.F1WebSite.services.QualifyingService;
import com.tp.F1WebSite.services.RaceService;
import com.tp.F1WebSite.services.ResultService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/races")
public class RaceController {

    private final RaceService raceService;
    private final ResultService resultService;
    private final QualifyingService qualifyingService;

    public RaceController(RaceService raceService, ResultService resultService, QualifyingService qualifyingService) {
        this.raceService = raceService;
        this.resultService = resultService;
        this.qualifyingService = qualifyingService;
    }

    // GET
    @GetMapping(path = "")
    public Page<RaceCircuitDto> getManyRaces(@RequestParam(required = false) String searchName,
                                             Pageable pageable) {
        Page<RaceCircuitDto> races;

        if (searchName != null && !searchName.isEmpty()) {
            races = raceService.findManyByName(searchName, pageable);
        } else {
            races = raceService.findManyByName("", pageable);
        }

        return races;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<RaceDto> getOneRace(@PathVariable("id") Long raceId) {
        return new ResponseEntity<>(raceService.findOneById(raceId), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/results")
    public List<ResultDto> getManyResults(@PathVariable("id") Long raceId) {
        return resultService.findMany(raceId);
    }

    @GetMapping(path = "/{id}/qualifying")
    public List<QualifyingDto> getManyQualifying(@PathVariable("id") Long raceId) {
        return qualifyingService.findMany(raceId);
    }


    // POST
    @PostMapping(path = "")
    public ResponseEntity<RaceDto> createRace(@Valid @RequestBody RaceCreationDto raceCreationDto) {
        return new ResponseEntity<>(raceService.createOne(raceCreationDto), HttpStatus.CREATED);
    }

    @PostMapping(path = "/{id}/results")
    public ResponseEntity<ResultDto> createResult(@PathVariable("id") Long raceId,
                                                  @Valid @RequestBody ResultCreationDto resultCreationDto) {
        return new ResponseEntity<>(resultService.createOne(raceId, resultCreationDto), HttpStatus.CREATED);
    }

    @PostMapping(path = "/{id}/qualifying")
    public ResponseEntity<QualifyingDto> createQualifying(@PathVariable("id") Long raceId,
                                                          @Valid @RequestBody QualifyingCreationDto qualifyingCreationDto) {
        return new ResponseEntity<>(qualifyingService.createOne(raceId, qualifyingCreationDto), HttpStatus.CREATED);
    }
}
