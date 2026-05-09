package com.tp.F1WebSite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CircuitRacesDto {

    private String name;

    private String country;

    private Long numOfRaces;
}
