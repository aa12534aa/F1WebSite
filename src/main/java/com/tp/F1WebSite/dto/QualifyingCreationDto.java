package com.tp.F1WebSite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QualifyingCreationDto {

    @NotBlank
    private String driverUrl;

    @NotBlank
    private String constructorName;

    @NotNull
    private Integer position;
}
