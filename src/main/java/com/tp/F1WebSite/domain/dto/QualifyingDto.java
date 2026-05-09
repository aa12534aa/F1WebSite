package com.tp.F1WebSite.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QualifyingDto {

    private Long qualifyingId;

    private RaceDto race;

    private DriverDto driver;

    private ConstructorDto constructor;

    private Integer position;
}
