package com.tp.F1WebSite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConstructorAllInfoDto {

    private String name;

    private Long firstPlaces;

    private Long secondPlaces;

    private Long thirdPlaces;

    private Double gainedPoints;

    private Long numOfRaces;

    private List<ConstructorRaceDto> races;

    public ConstructorAllInfoDto(String name, Long firstPlaces, Long secondPlaces, Long thirdPlaces, Double gainedPoints, Long numOfRaces) {
        this.name = name;
        this.firstPlaces = firstPlaces;
        this.secondPlaces = secondPlaces;
        this.thirdPlaces = thirdPlaces;
        this.gainedPoints = gainedPoints;
        this.numOfRaces = numOfRaces;
    }
}
