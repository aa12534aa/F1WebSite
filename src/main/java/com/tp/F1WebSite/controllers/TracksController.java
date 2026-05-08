package com.tp.F1WebSite.controllers;

import com.tp.F1WebSite.domain.dto.RaceDto;
import com.tp.F1WebSite.domain.entities.RaceEntity;
import com.tp.F1WebSite.dto.TracksRacesDto;
import com.tp.F1WebSite.mappers.Mapper;
import com.tp.F1WebSite.services.RaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TracksController {

    private RaceService raceService;

    private Mapper<RaceEntity, RaceDto> raceMapper;

    public TracksController(RaceService raceService, Mapper<RaceEntity, RaceDto> raceMapper) {
        this.raceService = raceService;
        this.raceMapper = raceMapper;
    }

    @GetMapping(path = "/tracks")
    public Page<TracksRacesDto> getManyTracks(@RequestParam(required = false) String searchTrack, Pageable pageable) {
        Page<TracksRacesDto> tracks;

        if (searchTrack != null && !searchTrack.isEmpty()) {
            tracks = raceService.findManyByName(searchTrack, pageable);
        } else {
            tracks = raceService.findManyByName("", pageable);
        }

        return tracks;
    }
}
