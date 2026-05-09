package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.dto.CircuitRacesDto;
import com.tp.F1WebSite.repositories.RaceRepository;
import com.tp.F1WebSite.services.RaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RaceServiceImpl implements RaceService {

    private final RaceRepository raceRepository;

    public RaceServiceImpl(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }
}
