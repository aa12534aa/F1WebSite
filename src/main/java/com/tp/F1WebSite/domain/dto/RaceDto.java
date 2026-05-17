package com.tp.F1WebSite.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceDto {

    private Long raceId;

    private CircuitDto circuit;

    private LocalDate date;

    private String name;
}
