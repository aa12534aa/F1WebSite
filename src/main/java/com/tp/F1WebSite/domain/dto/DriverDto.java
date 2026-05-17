package com.tp.F1WebSite.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDto {

    private Long driverId;

    @NotBlank
    private String name;

    @NotBlank
    private String nationality;

    @NotBlank
    private String url;
}
