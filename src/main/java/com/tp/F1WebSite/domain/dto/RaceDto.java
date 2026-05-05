package com.tp.F1WebSite.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceDto {

    private Long raceId;

    private Integer year;

    private String name;
}
