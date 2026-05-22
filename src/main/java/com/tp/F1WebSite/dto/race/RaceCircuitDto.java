package com.tp.F1WebSite.dto.race;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaceCircuitDto {

    private Long raceId;

    private String raceName;

    private LocalDate date;

    private String circuitName;

    private String country;

    private String winnerName;

    private String constructorName;
}
