package com.tp.F1WebSite.services;

import com.tp.F1WebSite.dto.TracksRacesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RaceService {


    Page<TracksRacesDto> findManyByName(String searchTrack, Pageable pageable);
}
