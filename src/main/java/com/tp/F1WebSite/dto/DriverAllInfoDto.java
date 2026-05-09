package com.tp.F1WebSite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class DriverAllInfoDto {

    private String name;

    private String url;

    private Long firstPlaces;

    private Long secondPlaces;

    private Long thirdPlaces;

    private Double gainedPoints;

    private Long numOfRaces;

    private Long numOfPolePosition;

    private List<DriverTrackWins> bestTracks;

    private List<DriverRaceDto> races;

    public DriverAllInfoDto(String name, String url, Long firstPlaces, Long secondPlaces, Long thirdPlaces, Double gainedPoints, Long numOfRaces) {
        this.name = name;
        this.url = url;
        this.firstPlaces = firstPlaces;
        this.secondPlaces = secondPlaces;
        this.thirdPlaces = thirdPlaces;
        this.gainedPoints = gainedPoints;
        this.numOfRaces = numOfRaces;
    }
}
