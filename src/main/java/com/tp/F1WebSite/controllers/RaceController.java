package com.tp.F1WebSite.controllers;

import com.tp.F1WebSite.domain.dto.QualifyingDto;
import com.tp.F1WebSite.domain.dto.RaceDto;
import com.tp.F1WebSite.domain.dto.ResultDto;
import com.tp.F1WebSite.domain.dto.SprintResultDto;
import com.tp.F1WebSite.dto.race.*;
import com.tp.F1WebSite.services.QualifyingService;
import com.tp.F1WebSite.services.RaceService;
import com.tp.F1WebSite.services.ResultService;
import com.tp.F1WebSite.services.SprintResultService;
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
    private final SprintResultService sprintResultService;

    public RaceController(RaceService raceService, ResultService resultService, QualifyingService qualifyingService, SprintResultService sprintResultService) {
        this.raceService = raceService;
        this.resultService = resultService;
        this.qualifyingService = qualifyingService;
        this.sprintResultService = sprintResultService;
    }

    // GET
    @GetMapping(path = "")
    public Page<RaceCircuitDto> getManyRaces(@RequestParam(required = false) String searchName,
                                             Boolean sprint,
                                             Pageable pageable) {
        Page<RaceCircuitDto> races;

        if (searchName != null && !searchName.isEmpty()) {
            races = sprint == null ? raceService.findManyByName(searchName, pageable) :
                raceService.findManyByNameAndSprint(searchName, sprint, pageable);
        } else {
            races = sprint == null ? raceService.findManyByName("", pageable) :
                    raceService.findManyByNameAndSprint("", sprint, pageable);
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

    @GetMapping(path = "/{id}/sprint-results")
    public List<SprintResultDto> getManySprintResults(@PathVariable("id") Long raceId) {
        return sprintResultService.findMany(raceId);
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

    @PostMapping(path = "/{id}/sprint-results")
    public ResponseEntity<SprintResultDto> createSprintResult(@PathVariable("id") Long raceId,
                                                  @Valid @RequestBody SprintResultCreationDto sprintResultCreationDto) {
        return new ResponseEntity<>(sprintResultService.createOne(raceId, sprintResultCreationDto), HttpStatus.CREATED);
    }

    // DELETE
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteRace(@PathVariable("id") Long raceId) {
        raceService.deleteOne(raceId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(path = "/{raceId}/results/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable("raceId") Long raceId,
                                             @PathVariable("id") Long resultId) {
        resultService.deleteOne(resultId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(path = "/{raceId}/qualifying/{id}")
    public ResponseEntity<Void> deleteQualifying(@PathVariable("raceId") Long raceId,
                                                 @PathVariable("id") Long qualifyingId) {
        qualifyingService.deleteOne(qualifyingId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(path = "/{raceId}/sprint-results/{id}")
    public ResponseEntity<Void> deleteSprintResult(@PathVariable("raceId") Long raceId,
                                             @PathVariable("id") Long sprintResultId) {
        sprintResultService.deleteOne(sprintResultId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // PUT
    @PutMapping(path = "/{id}")
    public ResponseEntity<RaceDto> updateRace(@PathVariable("id") Long raceId,
                                              @Valid @RequestBody RaceCreationDto raceCreationDto) {
        RaceDto updatedRace = raceService.updateOne(raceId, raceCreationDto);

        return new ResponseEntity<>(updatedRace, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{raceId}/results/{id}")
    public ResponseEntity<ResultDto> updateResult(@PathVariable("raceId") Long raceId,
                                                  @PathVariable("id") Long resultId,
                                              @Valid @RequestBody ResultCreationDto resultCreationDto) {
        ResultDto updateResult = resultService.updateOne(raceId, resultId, resultCreationDto);

        return new ResponseEntity<>(updateResult, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{raceId}/qualifying/{id}")
    public ResponseEntity<QualifyingDto> updateQualifying(@PathVariable("raceId") Long raceId,
                                                  @PathVariable("id") Long qualifyingId,
                                                  @Valid @RequestBody QualifyingCreationDto qualifyingCreationDto) {
        QualifyingDto updatedQualifying = qualifyingService.updateOne(raceId, qualifyingId, qualifyingCreationDto);

        return new ResponseEntity<>(updatedQualifying, HttpStatus.CREATED);
    }

    @PutMapping(path = "/{raceId}/sprint-results/{id}")
    public ResponseEntity<SprintResultDto> updateSprintResult(@PathVariable("raceId") Long raceId,
                                                  @PathVariable("id") Long sprintResultId,
                                                  @Valid @RequestBody SprintResultCreationDto sprintResultCreationDto) {
        SprintResultDto updateResult = sprintResultService.updateOne(raceId, sprintResultId, sprintResultCreationDto);

        return new ResponseEntity<>(updateResult, HttpStatus.CREATED);
    }
}
