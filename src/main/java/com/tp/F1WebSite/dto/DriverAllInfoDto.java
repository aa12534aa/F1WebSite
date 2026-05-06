package com.tp.F1WebSite.dto;

import com.tp.F1WebSite.domain.entities.RaceEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class DriverAllInfoDto {

    private Long id;

    private String name;

    private String url;

    private Long firstPlaces;

    private Long secondPlaces;

    private Long thirdPlaces;

    private Long gainedPoints;

    private Long numOfRaces;

    private List<DriverRacesDto> races;

    public DriverAllInfoDto(Long id, String name, String url, Long firstPlaces, Long secondPlaces, Long thirdPlaces, Long gainedPoints, Long numOfRaces) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.firstPlaces = firstPlaces;
        this.secondPlaces = secondPlaces;
        this.thirdPlaces = thirdPlaces;
        this.gainedPoints = gainedPoints;
        this.numOfRaces = numOfRaces;
    }
}
