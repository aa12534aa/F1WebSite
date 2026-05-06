package com.tp.F1WebSite.dto;

import com.tp.F1WebSite.domain.entities.ResultEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverRacesDto {

    private Long id;

    private Integer grid;

    private  Integer position;

    private Integer points;

    private Integer raceYear;

    private String raceName;
}
