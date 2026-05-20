package com.tp.F1WebSite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverCircuitsWins {

    private Long circuitId;

    private String circuitName;

    private String country;

    private Long numOfWins;
}
