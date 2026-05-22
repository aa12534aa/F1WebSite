package com.tp.F1WebSite.dto.driver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverRaceDto {

    private String name;

    private String team;

    private Integer grid;

    private  Integer position;

    private Double points;

    private LocalDate date;

    private String raceName;

    private String country;
}
