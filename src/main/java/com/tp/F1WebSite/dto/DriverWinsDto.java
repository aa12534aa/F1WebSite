package com.tp.F1WebSite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DriverWinsDto {
    
    private Long id;

    private String name;

    private Long firstPlaces;
}
