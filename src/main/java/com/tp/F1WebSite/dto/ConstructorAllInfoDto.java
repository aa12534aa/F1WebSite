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
}
