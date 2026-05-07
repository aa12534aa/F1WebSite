package com.tp.F1WebSite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConstructorRaceDto {

    private String driverName;

    private Integer grid;

    private  Integer position;

    private Double points;

    private LocalDate date;

    private String raceName;
}
