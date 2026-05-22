package com.tp.F1WebSite.dto.circuit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CircuitAllInfoDto {

    private String circuitName;

    private String country;

    private String url;

    private Long numOfRaces;

    private List<CircuitDriverWinsDto> bestDrivers;

    public CircuitAllInfoDto(String circuitName, String country, String url, Long numOfRaces) {
        this.circuitName = circuitName;
        this.country = country;
        this.url = url;
        this.numOfRaces = numOfRaces;
    }
}
