package com.tp.F1WebSite.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CircuitDto {

    private Long circuitId;

    private String name;

    private String country;

    private String url;
}
