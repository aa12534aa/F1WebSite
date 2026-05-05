package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.repositories.RaceRepository;
import com.tp.F1WebSite.services.RaceService;
import org.springframework.stereotype.Service;

@Service
public class RaceServiceImpl implements RaceService {

    private RaceRepository raceRepository;

    public RaceServiceImpl(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }
}
