package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.dto.TracksRacesDto;
import com.tp.F1WebSite.repositories.RaceRepository;
import com.tp.F1WebSite.services.RaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RaceServiceImpl implements RaceService {

    private RaceRepository raceRepository;

    public RaceServiceImpl(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @Override
    public Page<TracksRacesDto> findManyByName(String searchTrack, Pageable pageable) {
        return raceRepository.findTracksRacesByName(searchTrack, pageable);
    }
}
