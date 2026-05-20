package com.tp.F1WebSite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CircuitDriverWinsDto {

    Long driverId;

    String driverName;

    String nationality;

    Long numOfWins;
}
