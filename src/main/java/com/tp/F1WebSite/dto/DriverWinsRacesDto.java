package com.tp.F1WebSite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DriverWinsRacesDto {

    private Long driverId;

    private String name;

    private Long numOfWins;

    private Long numOfRaces;
}
