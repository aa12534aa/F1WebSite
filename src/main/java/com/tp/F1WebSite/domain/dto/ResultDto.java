package com.tp.F1WebSite.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultDto {

    private Long resultId;

    private RaceDto race;

    private DriverDto driver;

    private ConstructorDto constructor;

    private Integer grid;

    private Integer position;

    private Double points;
}
